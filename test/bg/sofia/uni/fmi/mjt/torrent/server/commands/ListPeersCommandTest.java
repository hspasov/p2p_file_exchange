package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ListPeersCommandTest extends TorrentCommandTest {
    @Test
    public void testNoPeers() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(ListPeersCommand.COMMAND_NAME);
        TorrentCommand command = new ListPeersCommand();
        TorrentResponse response = command.execute(request);
        final String noPeersResponseContent = "0 0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(
            noPeersResponseContent,
            responseContent,
            "ListPeersCommand returned unexpected result when testing empty response!"
        );
    }

    @Test
    public void testOnePeer() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);

        PeerRequest request = new PeerRequest(ListPeersCommand.COMMAND_NAME);
        TorrentCommand command = new ListPeersCommand();
        TorrentResponse response = command.execute(request);
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);

        final String expectedResponseHeader = "0 1\n";
        final String expectedResponsePayload = String.join(
            ListPeersCommand.PEER_PROPERTY_SEPARATOR,
            TEST_PEER1.username(),
            TEST_PEER1.address(),
            Integer.toString(TEST_PEER1.port())
        ) + ListPeersCommand.ENTRY_SEPARATOR;
        final String expectedResponseContent = expectedResponseHeader + expectedResponsePayload;

        assertEquals(
            expectedResponseContent,
            responseContent,
            "ListPeersCommand returned unexpected result when testing with one peer!"
        );
    }

    @Test
    public void testTwoPeers() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER2);

        PeerRequest request = new PeerRequest(ListPeersCommand.COMMAND_NAME);
        TorrentCommand command = new ListPeersCommand();
        TorrentResponse response = command.execute(request);
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        List<String> responseLines = List.of(responseContent.split(ListPeersCommand.ENTRY_SEPARATOR));

        final int expectedResponseLinesSize = 3;
        assertEquals(
            expectedResponseLinesSize,
            responseLines.size(),
            "ListPeersCommand returned an unexpected number of lines!"
        );

        String responseHeader = responseLines.get(0);
        Set<String> responsePayload = new HashSet<>(responseLines.subList(1, responseLines.size()));

        final String expectedResponseHeader = "0 2";
        final Set<String> expectedResponsePayload = Set.of(
            String.join(
                ListPeersCommand.PEER_PROPERTY_SEPARATOR,
                TEST_PEER1.username(),
                TEST_PEER1.address(),
                Integer.toString(TEST_PEER1.port())
            ),
            String.join(
                ListPeersCommand.PEER_PROPERTY_SEPARATOR,
                TEST_PEER2.username(),
                TEST_PEER2.address(),
                Integer.toString(TEST_PEER2.port())
            )
        );

        assertEquals(expectedResponseHeader, responseHeader, "ListPeersCommand returned an unexpected header!");
        assertTrue(
            expectedResponsePayload.containsAll(responsePayload) &&
                responsePayload.containsAll(expectedResponsePayload),
            "ListPeersCommand returned an unexpected payload!"
        );
    }
}