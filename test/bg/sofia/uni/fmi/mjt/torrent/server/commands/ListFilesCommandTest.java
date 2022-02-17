package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListFilesCommandTest extends TorrentCommandTest {
    private static final Peer TEST_PEER2 = new Peer("test-peer2", "0.0.0.0", 4001);

    @Test
    public void testNoFiles() throws TorrentRequestException {
        PeerRequest request = new PeerRequest(ListFilesCommand.COMMAND_NAME);
        TorrentCommand command = new ListFilesCommand();
        TorrentResponse response = command.execute(request);
        final String noFilesResponseContent = "0 0\n";
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        assertEquals(
            noFilesResponseContent,
            responseContent,
            "ListFilesCommand returned unexpected result when testing empty response!"
        );
    }

    @Test
    public void testOneFile() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);
        filesAvailabilityInfo.setFilesAvailable(TEST_PEER1.username(), Set.of(new TorrentFile(TEST_FILE1)));

        PeerRequest request = new PeerRequest(ListFilesCommand.COMMAND_NAME);
        TorrentCommand command = new ListFilesCommand();
        TorrentResponse response = command.execute(request);
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);

        final String expectedResponseHeader = "0 1\n";
        final String expectedResponsePayload = TEST_PEER1.username() +
            ListFilesCommand.USER_FILEPATH_SEPARATOR +
            TEST_FILE1 +
            ListFilesCommand.ENTRY_SEPARATOR;
        final String expectedResponseContent = expectedResponseHeader + expectedResponsePayload;

        assertEquals(
            expectedResponseContent,
            responseContent,
            "ListFilesCommand returned unexpected result when testing with one file entry!"
        );
    }

    @Test
    public void testTwoFiles() throws TorrentRequestException {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER1);
        filesAvailabilityInfo.setPeerAvailable(TEST_PEER2);
        filesAvailabilityInfo.setFilesAvailable(TEST_PEER1.username(), Set.of(new TorrentFile(TEST_FILE1)));
        filesAvailabilityInfo.setFilesAvailable(TEST_PEER2.username(), Set.of(new TorrentFile(TEST_FILE2)));

        PeerRequest request = new PeerRequest(ListFilesCommand.COMMAND_NAME);
        TorrentCommand command = new ListFilesCommand();
        TorrentResponse response = command.execute(request);
        String responseContent = new String(response.getContent(), StandardCharsets.UTF_8);
        List<String> responseLines = List.of(responseContent.split(ListFilesCommand.ENTRY_SEPARATOR));

        final int expectedResponseLinesSize = 3;
        assertEquals(
            expectedResponseLinesSize,
            responseLines.size(),
            "ListFilesCommand returned an unexpected number of lines!"
        );

        String responseHeader = responseLines.get(0);
        Set<String> responsePayload = new HashSet<>(responseLines.subList(1, responseLines.size()));

        final String expectedResponseHeader = "0 2";
        final Set<String> expectedResponsePayload = Set.of(
            TEST_PEER1.username() + ListFilesCommand.USER_FILEPATH_SEPARATOR + TEST_FILE1,
            TEST_PEER2.username() + ListFilesCommand.USER_FILEPATH_SEPARATOR + TEST_FILE2
        );

        assertEquals(expectedResponseHeader, responseHeader, "ListFilesCommand returned an unexpected header!");
        assertTrue(
            expectedResponsePayload.containsAll(responsePayload) &&
                responsePayload.containsAll(expectedResponsePayload),
            "ListFilesCommand returned an unexpected payload!"
        );
    }
}