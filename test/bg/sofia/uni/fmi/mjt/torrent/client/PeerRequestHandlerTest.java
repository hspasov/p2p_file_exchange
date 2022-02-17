package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.client.commands.DownloadCommand;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PeerRequestHandlerTest {

    @Test
    public void testGetDownload() {
        TorrentCommand command = PeerRequestHandler.get(DownloadCommand.COMMAND_NAME);
        assertEquals(DownloadCommand.class, command.getClass(), "Wrong instance type returned!");

    }

    @Test
    public void testGetInvalidCommand() throws TorrentRequestException {
        final String invalidCommandName = "null";
        TorrentCommand invalidCommand = PeerRequestHandler.get(invalidCommandName);
        TorrentResponse response = invalidCommand.execute(null);

        final String expectedContent = "1\n";
        String actualContent = new String(response.getContent(), StandardCharsets.UTF_8);

        assertEquals(expectedContent, actualContent, "Invalid command returned unexpected content!");
    }
}