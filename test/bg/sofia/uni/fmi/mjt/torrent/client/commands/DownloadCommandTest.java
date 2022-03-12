package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DownloadCommandTest {
    private static final String TEST_FILE = "./downloaded_file.txt";
    private static final String SAMPLE_FILE_CONTENT = "Sample text";

    @BeforeAll
    public static void setUp() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEST_FILE),
            StandardCharsets.UTF_8
        ))) {
            writer.write(SAMPLE_FILE_CONTENT);
            writer.flush();
        }
    }

    @AfterAll
    public static void tearDown() {
        File file = new File(TEST_FILE);
        file.delete();
    }

    @Test
    public void testExecute() throws TorrentRequestException {
        PeerRequest request = new PeerRequest("download sample-user " + TEST_FILE);
        TorrentCommand command = new DownloadCommand();
        TorrentResponse response = command.execute(request);
        String contentDecoded = new String(response.getContent(), StandardCharsets.UTF_8);
        String expectedContent = "0 " + SAMPLE_FILE_CONTENT.length() + "\n" + SAMPLE_FILE_CONTENT;
        assertEquals(expectedContent, contentDecoded, "DownloadCommand did not give expected result!");
    }

    @Test
    public void testMissingFile() throws TorrentRequestException {
        PeerRequest request = new PeerRequest("download sample-user ./nonexistent-file.txt");
        TorrentCommand command = new DownloadCommand();
        TorrentResponse response = command.execute(request);
        String contentDecoded = new String(response.getContent(), StandardCharsets.UTF_8);
        String expectedContent = "1\n";
        assertEquals(expectedContent, contentDecoded, "DownloadCommand did not give expected result!");
    }

    @Test
    public void testMissingPayload() throws TorrentRequestException {
        PeerRequest request = new PeerRequest("download");
        TorrentCommand command = new DownloadCommand();
        assertThrows(
            TorrentRequestException.class,
            () -> command.execute(request),
            "DownloadCommand must throw when payload is not supplied!"
        );
    }
}