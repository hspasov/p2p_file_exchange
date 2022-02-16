package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TorrentClientTest {
    private final String PEERS_FILE = "./peers-file.txt";
    private final String TORRENT_SERVER_ADDRESS = "127.0.0.1";
    private final int TORRENT_SERVER_PORT = 3000;

    @Test
    public void startPeerRequestListener() throws IOException {
        TorrentClient torrentClient = new TorrentClient(PEERS_FILE, TORRENT_SERVER_ADDRESS, TORRENT_SERVER_PORT);
        Peer peer = torrentClient.startPeerRequestListener();
        assertNotNull(peer, "Peer must not be null!");
        assertNull(peer.username(), "Peer username must be null when client is just initialized!");
    }
}