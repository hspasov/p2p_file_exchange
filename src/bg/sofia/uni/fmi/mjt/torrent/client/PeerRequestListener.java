package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerRequestListener implements Runnable {
    private static final int MAX_EXECUTOR_THREADS = 100;
    private static final int PEER_CONNECTION_TIMEOUT_MS = 10_000;
    private final ServerSocket serverSocket;

    public PeerRequestListener(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        Logger logger = TorrentClient.getLogger();
        logger.log(Level.INFO, LocalDateTime.now(), "Listening on " + this.serverSocket.getLocalPort() + "...");

        while (true) {
            try {
                Socket peerSocket = this.serverSocket.accept();
                peerSocket.setSoTimeout(PEER_CONNECTION_TIMEOUT_MS);
                logger.log(
                    Level.INFO,
                    LocalDateTime.now(),
                    "Accepted connection request from peer " + peerSocket.getInetAddress()
                );
                PeerRequestHandler peerHandler = new PeerRequestHandler(peerSocket);
                executor.execute(peerHandler);
            } catch (IOException e) {
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                logger.log(Level.ERROR, LocalDateTime.now(), writer.toString());
            }
        }
    }
}
