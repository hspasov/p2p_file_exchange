package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class TorrentClient {
    public static final String TORRENT_SERVER_ADDRESS = "127.0.0.1";
    // TODO set port as parameter
    public static final int TORRENT_SERVER_PORT = 3000;
    // TODO set peers file as parameter
    public static final String PEERS_FILE = "./peers_file.txt";

    private void startFetchPeersTimer() {
        Thread fetchPeersTimerThread = new Thread(new FetchPeersTimer());
        fetchPeersTimerThread.setDaemon(true);
        fetchPeersTimerThread.start();
    }

    private Peer startPeerRequestListener() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            Thread peerRequestListenerThread = new Thread(new PeerRequestListener(serverSocket));
            peerRequestListenerThread.setDaemon(true);
            peerRequestListenerThread.start();
            return new Peer(null, serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
        }
    }

    private void listenForUserInput(Peer peer) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // TODO validate all commands - forbid extra space, forbid space in usernames
            System.out.print("TorrentClient=# ");
            String command = scanner.nextLine();
            System.out.println("Command read: " + command);
        }
    }

    public static void main(String[] args) {
        TorrentClient client = new TorrentClient();
        // TODO run hello command
        // TODO and listen for commands from other peers

        try {
            client.startFetchPeersTimer();
            Peer peer = client.startPeerRequestListener();
            client.listenForUserInput(peer);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }
}
