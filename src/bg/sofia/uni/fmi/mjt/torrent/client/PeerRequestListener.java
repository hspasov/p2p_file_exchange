package bg.sofia.uni.fmi.mjt.torrent.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerRequestListener implements Runnable {
    private static final int MAX_EXECUTOR_THREADS = 100;

    @Override
    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            System.out.println("Listening on " + serverSocket.getLocalPort() + "...");

            while (true) {
                Socket peerSocket = serverSocket.accept();

                System.out.println("Accepted connection request from peer " + peerSocket.getInetAddress());

                PeerRequestHandler peerHandler = new PeerRequestHandler(peerSocket);

                executor.execute(peerHandler);
            }
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }
}
