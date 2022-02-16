package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HelloCommandTest {
    private static final String COMMAND_PARTS_SEPARATOR = " ";
    private static final String COMMAND_NAME = "hello";
    private static final String TEST_USERNAME = "test-peer";
    private static final String TEST_ADDRESS = "0.0.0.0";
    private static final String PAYLOAD_PARTS_SEPARATOR = ":";

    @BeforeEach
    private void setUp() {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.reset();
    }

    @Test
    public void testEmptyUsername() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(COMMAND_NAME);
        TorrentCommand command = new HelloCommand();
        assertThrows(
            TorrentRequestException.class,
            () -> command.execute(request),
            "Expected hello command to throw TorrentRequestException when no user is provided!"
        );
    }

    @Test
    public void testEmptyPayload() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(String.join(COMMAND_PARTS_SEPARATOR, COMMAND_NAME, TEST_USERNAME));
        TorrentCommand command = new HelloCommand();
        assertThrows(
            TorrentRequestException.class,
            () -> command.execute(request),
            "Expected hello command to throw TorrentRequestException when no port is provided!"
        );
    }

    @Test
    public void testInvalidPort() throws TorrentRequestException {
        final String invalidPort = "null";
        PeerRequest request = new PeerRequest(
            String.join(
                COMMAND_PARTS_SEPARATOR,
                COMMAND_NAME,
                TEST_USERNAME,
                String.join(PAYLOAD_PARTS_SEPARATOR, TEST_ADDRESS, invalidPort)
            )
        );
        TorrentCommand command = new HelloCommand();
        assertThrows(
            TorrentRequestException.class,
            () -> command.execute(request),
            "Expected hello command to throw TorrentRequestException when invalid port is provided!"
        );
    }

    @Test
    public void testExecute() throws TorrentRequestException {
        final int port = 10000;
        PeerRequest request = new PeerRequest(
            String.join(
                COMMAND_PARTS_SEPARATOR,
                COMMAND_NAME,
                TEST_USERNAME,
                String.join(PAYLOAD_PARTS_SEPARATOR, TEST_ADDRESS, Integer.toString(port))
            )
        );
        TorrentCommand command = new HelloCommand();
        TorrentResponse response = command.execute(request);
        final String expectedResponseContent = "0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);

        assertEquals(expectedResponseContent, responseContent, "Unexpected response from hello command!");

        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        Map<String, Peer> availablePeers = filesAvailabilityInfo.getAvailablePeers();
        Peer peer = availablePeers.get(TEST_USERNAME);

        assertNotNull(peer, "Peer must not be null!");
        assertEquals(TEST_USERNAME, peer.username(), "Unexpected username of saved peer!");
        assertEquals(port, peer.port(), "Unexpected port of saved peer!");
        assertEquals(TEST_ADDRESS, peer.address(), "Unexpected address of saved peer!");
    }
}