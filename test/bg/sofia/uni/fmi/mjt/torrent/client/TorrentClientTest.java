package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.logger.DefaultLogger;
import bg.sofia.uni.fmi.mjt.logger.LoggerOptions;
import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TorrentClientTest {
    private static final String PEERS_FILE = "./peers-file.txt";
    private static final String TORRENT_SERVER_ADDRESS = "127.0.0.1";
    private static final int TORRENT_SERVER_PORT = 3000;
    private static final ServerEndpoint SERVER_ENDPOINT = new ServerEndpoint(
        TORRENT_SERVER_ADDRESS, TORRENT_SERVER_PORT
    );

    @Test
    public void startPeerRequestListener() throws IOException {
        TorrentClient torrentClient = new TorrentClient(PEERS_FILE, SERVER_ENDPOINT);
        TorrentClient.setLogger(new DefaultLogger(new LoggerOptions(TorrentClient.class, ".")));
        Peer peer = torrentClient.startPeerRequestListener();
        assertNotNull(peer, "Peer must not be null!");
        assertNull(peer.username(), "Peer username must be null when client is just initialized!");
    }
}