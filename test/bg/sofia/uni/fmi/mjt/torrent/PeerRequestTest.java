package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PeerRequestTest {
    @Test
    public void testInvalidCommand() {
        assertThrows(
            TorrentRequestException.class,
            () -> new PeerRequest(null),
            "PeerRequest did not throw the expected type of exception on null command!"
        );
    }

    @Test
    public void testCommandWithoutUsername() throws TorrentRequestException {
        String command = "command";
        PeerRequest request = new PeerRequest(command);
        assertEquals(command, request.command(), "Command does not match!");
        assertNull(request.username(), "Expected username to be null!");
        assertNull(request.payload(), "Expected payload to be null!");
    }

    @Test
    public void testCommandWithoutPayload() throws TorrentRequestException {
        String command = "command";
        String username = "user";
        PeerRequest request = new PeerRequest(String.join(" ", command, username));
        assertEquals(command, request.command(), "Command does not match!");
        assertEquals(username, request.username(), "Username does not match!");
        assertNull(request.payload(), "Expected payload to be null!");
    }

    @Test
    public void testCommandWithPayload() throws TorrentRequestException {
        String command = "command";
        String username = "user";
        String payload = "sample payload";
        String rawCommand = String.join(" ", command, username, payload);
        PeerRequest request = new PeerRequest(rawCommand);
        assertEquals(command, request.command(), "Command does not match!");
        assertEquals(username, request.username(), "Username does not match!");
        assertEquals(payload, request.payload(), "Payload does not match!");
        assertEquals(rawCommand, request.rawCommand(), "Raw command does not match!");
    }
}