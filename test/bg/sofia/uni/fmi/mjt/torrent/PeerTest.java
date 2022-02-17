package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.InvalidPeerException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PeerTest {

    @Test
    public void testCreateWithValidInput() throws InvalidPeerException {
        final String validInput = "test 0.0.0.0 4001";
        Peer peer = Peer.of(validInput);
        assertNotNull(peer, "Expected peer to be created!");
    }

    @Test
    public void testCreateWithInvalidPort() {
        final String invalidInput = "test 0.0.0.0 null";
        assertThrows(
            InvalidPeerException.class,
            () -> Peer.of(invalidInput),
            "Expected to throw because of invalid port!"
        );
    }

    @Test
    public void testCreateWithInvalidInputParts() {
        final String invalidInput = "test 0.0.0.0";
        assertThrows(
            InvalidPeerException.class,
            () -> Peer.of(invalidInput),
            "Expected to throw because of invalid input parts!"
        );
    }
}