package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PeersAvailabilityInfoTest {
    private static final String PEERS_FILE = "./peers-file.txt";
    private static final String PEER_ADDRESS_SEPARATOR = " - ";
    private static final String ADDRESS_PORT_SEPARATOR = ":";
    private static final String ENTRY_SEPARATOR = "\n";

    @AfterAll
    static void tearDown() {
        File file = new File(PEERS_FILE);
        file.delete();
    }

    @Test
    public void testPeersAvailabilityInfo() throws IOException {
        final String peer1 = "peer1";
        final String peer2 = "peer2";
        final String address = "0.0.0.0";
        final int port1 = 4000;
        final int port2 = 4001;

        Map<String, Peer> availablePeers = new HashMap<>();
        availablePeers.put(peer1, new Peer(peer1, address, port1));
        availablePeers.put(peer2, new Peer(peer2, address, port2));

        PeersAvailabilityInfo.setPeersFile(PEERS_FILE);
        PeersAvailabilityInfo peersAvailabilityInfo = PeersAvailabilityInfo.getInstance();
        peersAvailabilityInfo.setAvailablePeers(availablePeers);

        Set<String> expectedFileContent = Set.of(
            peer1 + PEER_ADDRESS_SEPARATOR + address + ADDRESS_PORT_SEPARATOR + port1,
            peer2 + PEER_ADDRESS_SEPARATOR + address + ADDRESS_PORT_SEPARATOR + port2
        );
        Set<String> fileContent = Set.of(Files.readString(Path.of(PEERS_FILE)).split(ENTRY_SEPARATOR));
        assertTrue(
            expectedFileContent.containsAll(fileContent) && fileContent.containsAll(expectedFileContent),
            "Unexpected peers file content!"
        );

        Peer peer = peersAvailabilityInfo.getPeer(peer1);
        assertEquals(peer1, peer.username(), "Peer username does not match!");
        assertEquals(address, peer.address(), "Peer address does not match!");
        assertEquals(port1, peer.port(), "Peer port does not match!");
        assertNull(peersAvailabilityInfo.getPeer("N/A"), "Expected null for nonexistent peer!");
    }
}