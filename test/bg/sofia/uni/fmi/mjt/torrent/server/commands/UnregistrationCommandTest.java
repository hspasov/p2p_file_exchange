package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UnregistrationCommandTest extends TorrentCommandTest {
    @Test
    public void testNoUsername() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(UnregistrationCommand.COMMAND_NAME);
        TorrentCommand command = new UnregistrationCommand();
        assertThrows(
            TorrentRequestException.class,
            () -> command.execute(request),
            "Expected UnregistrationCommand to throw TorrentRequestException when no username provided!"
        );
    }

    @Test
    public void testNoPayload() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(
            UnregistrationCommand.COMMAND_NAME + COMMAND_PARTS_SEPARATOR + TEST_PEER1.username()
        );
        TorrentCommand command = new UnregistrationCommand();
        assertThrows(
            TorrentRequestException.class,
            () -> command.execute(request),
            "Expected UnregistrationCommand to throw TorrentRequestException when no username provided!"
        );
    }

    @Test
    public void testUnregisterWithoutPeerAvailable() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(
            String.join(COMMAND_PARTS_SEPARATOR, UnregistrationCommand.COMMAND_NAME, TEST_PEER1.username(), TEST_FILE1)
        );
        TorrentCommand command = new UnregistrationCommand();
        TorrentResponse response = command.execute(request);

        final String expectedResponseContent = "0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(expectedResponseContent, responseContent, "Unexpected response from unregister command!");
    }

    @Test
    public void testUnregisterWithoutFileRegistered() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);
        filesAvailabilityInfo.setFilesAvailable(TEST_PEER1.username(), Set.of(new TorrentFile(TEST_FILE1)));

        PeerRequest request = new PeerRequest(
            String.join(COMMAND_PARTS_SEPARATOR, UnregistrationCommand.COMMAND_NAME, TEST_PEER1.username(), TEST_FILE2)
        );
        TorrentCommand command = new UnregistrationCommand();
        TorrentResponse response = command.execute(request);

        final String expectedResponseContent = "0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(expectedResponseContent, responseContent, "Unexpected response from unregister command!");

        Map<String, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getAvailableFiles();
        Set<TorrentFile> availableFiles = usersAvailableFiles.get(TEST_PEER1.username());

        assertNotNull(availableFiles, "Available files must not be null after unregister command!");

        final int expectedAvailableFilesSize = 1;
        assertEquals(
            expectedAvailableFilesSize,
            availableFiles.size(),
            "Unexpected number of available files!"
        );
        assertTrue(availableFiles.contains(new TorrentFile(TEST_FILE1)), "Wrong file unregistered!");
        assertFalse(availableFiles.contains(new TorrentFile(TEST_FILE2)), "File available after unregister!");
    }

    @Test
    public void testUnRegisterOneFile() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);
        filesAvailabilityInfo.setFilesAvailable(
            TEST_PEER1.username(),
            Set.of(new TorrentFile(TEST_FILE1), new TorrentFile(TEST_FILE2))
        );

        PeerRequest request = new PeerRequest(
            String.join(COMMAND_PARTS_SEPARATOR, UnregistrationCommand.COMMAND_NAME, TEST_PEER1.username(), TEST_FILE1)
        );
        TorrentCommand command = new UnregistrationCommand();
        TorrentResponse response = command.execute(request);

        final String expectedResponseContent = "0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(expectedResponseContent, responseContent, "Unexpected response from unregister command!");

        Map<String, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getAvailableFiles();
        Set<TorrentFile> availableFiles = usersAvailableFiles.get(TEST_PEER1.username());

        assertNotNull(availableFiles, "Available files must not be null after unregister command!");

        final int expectedAvailableFilesSize = 1;
        assertEquals(
            expectedAvailableFilesSize,
            availableFiles.size(),
            "Unexpected number of available files!"
        );
        assertFalse(availableFiles.contains(new TorrentFile(TEST_FILE1)), "File was not unregistered!");
        assertTrue(availableFiles.contains(new TorrentFile(TEST_FILE2)), "Wrong file was unregistered!");
    }

    @Test
    public void testUnregisterTwoFiles() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);
        filesAvailabilityInfo.setFilesAvailable(
            TEST_PEER1.username(),
            Set.of(new TorrentFile(TEST_FILE1), new TorrentFile(TEST_FILE2))
        );

        PeerRequest request = new PeerRequest(
            String.join(
                COMMAND_PARTS_SEPARATOR,
                UnregistrationCommand.COMMAND_NAME,
                TEST_PEER1.username(),
                String.join(
                    UnregistrationCommand.FILES_SEPARATOR,
                    TEST_FILE1,
                    TEST_FILE2
                )
            )
        );
        TorrentCommand command = new UnregistrationCommand();
        TorrentResponse response = command.execute(request);

        final String expectedResponseContent = "0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(expectedResponseContent, responseContent, "Unexpected response from unregister command!");

        Map<String, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getAvailableFiles();
        Set<TorrentFile> availableFiles = usersAvailableFiles.get(TEST_PEER1.username());

        assertNotNull(availableFiles, "Available files must not be null after unregister command!");

        final int expectedAvailableFilesSize = 0;
        assertEquals(
            expectedAvailableFilesSize,
            availableFiles.size(),
            "Unexpected number of available files!"
        );
        assertFalse(availableFiles.contains(new TorrentFile(TEST_FILE1)), "First file was not unregistered!");
        assertFalse(availableFiles.contains(new TorrentFile(TEST_FILE2)), "Second file was not unregistered!");
    }
}