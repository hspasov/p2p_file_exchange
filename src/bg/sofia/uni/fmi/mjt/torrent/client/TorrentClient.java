package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;

import java.io.IOException;
import java.net.ServerSocket;

public class TorrentClient {
    private static final int ARG_LENGTH = 3;
    private static final int ARG_TORRENT_SERVER_ADDRESS_IDX = 0;
    private static final int ARG_TORRENT_SERVER_PORT_IDX = 1;
    private static final int ARG_PEERS_FILE_IDX = 2;

    private final ServerEndpoint serverEndpoint;

    public TorrentClient(String peersFile, ServerEndpoint serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
        PeersAvailabilityInfo.setPeersFile(peersFile);
    }

    public Thread startFetchPeersTimer(long fetchIntervalMs) {
        Thread fetchPeersTimerThread = new Thread(new FetchPeersTimer(fetchIntervalMs, this.serverEndpoint));
        fetchPeersTimerThread.setDaemon(true);
        fetchPeersTimerThread.start();
        return fetchPeersTimerThread;
    }

    public Peer startPeerRequestListener() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        Thread peerRequestListenerThread = new Thread(new PeerRequestListener(serverSocket));
        peerRequestListenerThread.setDaemon(true);
        peerRequestListenerThread.start();
        return new Peer(null, serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }

    public void listenForUserInput(Peer peer) {
        UserInputListener userInputListener = new UserInputListener(peer, this.serverEndpoint);
        userInputListener.run();
    }

    public static void main(String[] args) {
        if (args.length < ARG_LENGTH) {
            throw new IllegalArgumentException("Invalid arg count!");
        }
        String torrentServerAddress = args[ARG_TORRENT_SERVER_ADDRESS_IDX];
        int torrentServerPort = Integer.parseInt(args[ARG_TORRENT_SERVER_PORT_IDX]);
        String peersFile = args[ARG_PEERS_FILE_IDX];

        ServerEndpoint serverEndpoint = new ServerEndpoint(torrentServerAddress, torrentServerPort);
        TorrentClient client = new TorrentClient(peersFile, serverEndpoint);
        try {
            final long fetchPeersIntervalMs = 30_000;
            client.startFetchPeersTimer(fetchPeersIntervalMs);
            Peer peer = client.startPeerRequestListener();
            client.listenForUserInput(peer);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }
}
