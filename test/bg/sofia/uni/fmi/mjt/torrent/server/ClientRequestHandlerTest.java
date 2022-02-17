package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.client.PeerRequestHandler;
import bg.sofia.uni.fmi.mjt.torrent.client.commands.DownloadCommand;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.HelloCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListFilesCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListPeersCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.RegistrationCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.UnregistrationCommand;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ClientRequestHandlerTest {

    @Test
    public void testGetHello() {
        TorrentCommand command = ClientRequestHandler.get(HelloCommand.COMMAND_NAME);
        assertEquals(HelloCommand.class, command.getClass(), "Wrong instance type returned!");
    }

    @Test
    public void testGetRegistration() {
        TorrentCommand command = ClientRequestHandler.get(RegistrationCommand.COMMAND_NAME);
        assertEquals(RegistrationCommand.class, command.getClass(), "Wrong instance type returned!");
    }

    @Test
    public void testGetUnregistration() {
        TorrentCommand command = ClientRequestHandler.get(UnregistrationCommand.COMMAND_NAME);
        assertEquals(UnregistrationCommand.class, command.getClass(), "Wrong instance type returned!");
    }

    @Test
    public void testGetListFiles() {
        TorrentCommand command = ClientRequestHandler.get(ListFilesCommand.COMMAND_NAME);
        assertEquals(ListFilesCommand.class, command.getClass(), "Wrong instance type returned!");
    }

    @Test
    public void testGetListPeers() {
        TorrentCommand command = ClientRequestHandler.get(ListPeersCommand.COMMAND_NAME);
        assertEquals(ListPeersCommand.class, command.getClass(), "Wrong instance type returned!");
    }

    @Test
    public void testGetInvalidCommand() throws TorrentRequestException {
        final String invalidCommandName = "null";
        TorrentCommand invalidCommand = ClientRequestHandler.get(invalidCommandName);
        TorrentResponse response = invalidCommand.execute(null);

        final String expectedContent = "1\n";
        String actualContent = new String(response.getContent(), StandardCharsets.UTF_8);

        assertEquals(expectedContent, actualContent, "Invalid command returned unexpected content!");
    }
}