package bg.sofia.uni.fmi.mjt.torrent.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TorrentServer {
    private static final int SERVER_PORT = 3000;
    // TODO check what happens when max threads reached
    private static final int MAX_EXECUTOR_THREADS = 100;

    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Listening on " + SERVER_PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("Accepted connection request from client " +
                    clientSocket.getInetAddress().getHostAddress() +
                    ":" +
                    clientSocket.getPort()
                );

                ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket);

                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TorrentServer server = new TorrentServer();
        server.run();
    }
}
