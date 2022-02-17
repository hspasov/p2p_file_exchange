package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TorrentClientTest {
    private final String PEERS_FILE = "./peers-file.txt";
    private final String TORRENT_SERVER_ADDRESS = "127.0.0.1";
    private final int TORRENT_SERVER_PORT = 3000;
    private final ServerEndpoint SERVER_ENDPOINT = new ServerEndpoint(TORRENT_SERVER_ADDRESS, TORRENT_SERVER_PORT);

    @Test
    public void startPeerRequestListener() throws IOException {
        TorrentClient torrentClient = new TorrentClient(PEERS_FILE, SERVER_ENDPOINT);
        Peer peer = torrentClient.startPeerRequestListener();
        assertNotNull(peer, "Peer must not be null!");
        assertNull(peer.username(), "Peer username must be null when client is just initialized!");
    }

    @Test
    public void startFetchPeersTimer() {
        final int fetchIntervalMs = 10_000;
        TorrentClient torrentClient = new TorrentClient(PEERS_FILE, SERVER_ENDPOINT);
        Thread thread = torrentClient.startFetchPeersTimer(fetchIntervalMs);
        assertTrue(thread.isAlive(), "Expected thread to be alive!");
    }
}