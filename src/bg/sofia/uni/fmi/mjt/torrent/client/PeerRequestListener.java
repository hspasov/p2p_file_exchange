package bg.sofia.uni.fmi.mjt.torrent.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerRequestListener implements Runnable {
    private static final int MAX_EXECUTOR_THREADS = 100;
    private final ServerSocket serverSocket;

    public PeerRequestListener(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        System.out.println("Listening on " + this.serverSocket.getLocalPort() + "...");

        while (true) {
            try {
                Socket peerSocket = this.serverSocket.accept();
                System.out.println("Accepted connection request from peer " + peerSocket.getInetAddress());
                PeerRequestHandler peerHandler = new PeerRequestHandler(peerSocket);
                executor.execute(peerHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
