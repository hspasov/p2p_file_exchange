package bg.sofia.uni.fmi.mjt.torrent.client;

import java.util.Scanner;

public class TorrentClient {
    public static final String TORRENT_SERVER_ADDRESS = "127.0.0.1";
    // TODO set port as parameter
    public static final int TORRENT_SERVER_PORT = 3000;
    // TODO set peers file as parameter
    public static final String PEERS_FILE = "./peers_file.txt";

    void startFetchPeersTimer() {
        Thread fetchPeersTimerThread = new Thread(new FetchPeersTimer());
        fetchPeersTimerThread.setDaemon(true);
        fetchPeersTimerThread.start();
    }

    void listenForPeerInput() {

    }

    void listenForUserInput() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("TorrentClient=# ");
            String command = scanner.nextLine();
            System.out.println("Command read: " + command);
        }
    }

    public static void main(String[] args) {
        TorrentClient client = new TorrentClient();
        client.startFetchPeersTimer();
        client.listenForPeerInput();
        client.listenForUserInput();
    }
}
