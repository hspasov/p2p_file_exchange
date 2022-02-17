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

class RegistrationCommandTest extends TorrentCommandTest {

    @Test
    public void testNoUsername() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(RegistrationCommand.COMMAND_NAME);
        TorrentCommand command = new RegistrationCommand();
        assertThrows(
            TorrentRequestException.class,
            () -> command.execute(request),
            "Expected RegistrationCommand to throw TorrentRequestException when no username provided!"
        );
    }

    @Test
    public void testNoPayload() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(
            RegistrationCommand.COMMAND_NAME + COMMAND_PARTS_SEPARATOR + TEST_PEER1.username()
        );
        TorrentCommand command = new RegistrationCommand();
        assertThrows(
            TorrentRequestException.class,
            () -> command.execute(request),
            "Expected RegistrationCommand to throw TorrentRequestException when no username provided!"
        );
    }

    @Test
    public void testRegisterWithoutPeerAvailable() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(
            String.join(COMMAND_PARTS_SEPARATOR, RegistrationCommand.COMMAND_NAME, TEST_PEER1.username(), TEST_FILE1)
        );
        TorrentCommand command = new RegistrationCommand();
        TorrentResponse response = command.execute(request);

        final String expectedResponseContent = "0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(expectedResponseContent, responseContent, "Unexpected response from register command!");

        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        Map<String, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getAvailableFiles();

        assertNull(
            usersAvailableFiles.get(TEST_PEER1.username()),
            "Expected file to not be registered if the user had not sent hello command!"
        );
    }

    @Test
    public void testRegisterOneFile() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);

        PeerRequest request = new PeerRequest(
            String.join(COMMAND_PARTS_SEPARATOR, RegistrationCommand.COMMAND_NAME, TEST_PEER1.username(), TEST_FILE1)
        );
        TorrentCommand command = new RegistrationCommand();
        TorrentResponse response = command.execute(request);

        final String expectedResponseContent = "0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(expectedResponseContent, responseContent, "Unexpected response from register command!");

        Map<String, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getAvailableFiles();
        Set<TorrentFile> availableFiles = usersAvailableFiles.get(TEST_PEER1.username());

        assertNotNull(availableFiles, "Available files must not be null after register command!");

        final int expectedAvailableFilesSize = 1;
        assertEquals(
            expectedAvailableFilesSize,
            availableFiles.size(),
            "Unexpected number of available files!"
        );
        assertTrue(availableFiles.contains(new TorrentFile(TEST_FILE1)), "File was not registered!");
    }

    @Test
    public void testRegisterTwoFiles() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);

        PeerRequest request = new PeerRequest(
            String.join(
                COMMAND_PARTS_SEPARATOR,
                RegistrationCommand.COMMAND_NAME,
                TEST_PEER1.username(),
                String.join(
                    RegistrationCommand.FILES_SEPARATOR,
                    TEST_FILE1,
                    TEST_FILE2
                )
            )
        );
        TorrentCommand command = new RegistrationCommand();
        TorrentResponse response = command.execute(request);

        final String expectedResponseContent = "0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(expectedResponseContent, responseContent, "Unexpected response from register command!");

        Map<String, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getAvailableFiles();
        Set<TorrentFile> availableFiles = usersAvailableFiles.get(TEST_PEER1.username());

        assertNotNull(availableFiles, "Available files must not be null after register command!");

        final int expectedAvailableFilesSize = 2;
        assertEquals(
            expectedAvailableFilesSize,
            availableFiles.size(),
            "Unexpected number of available files!"
        );
        assertTrue(availableFiles.contains(new TorrentFile(TEST_FILE1)), "First file was not registered!");
        assertTrue(availableFiles.contains(new TorrentFile(TEST_FILE2)), "Second file was not registered!");
    }
}