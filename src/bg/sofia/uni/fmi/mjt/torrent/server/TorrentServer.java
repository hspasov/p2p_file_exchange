package bg.sofia.uni.fmi.mjt.torrent.server;


import bg.sofia.uni.fmi.mjt.logger.DefaultLogger;
import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;
import bg.sofia.uni.fmi.mjt.logger.LoggerOptions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TorrentServer {
    private static final int ARG_LENGTH = 2;
    private static final int ARG_PORT_IDX = 0;
    private static final int ARG_LOG_DIR_IDX = 1;

    private static final int MAX_EXECUTOR_THREADS = 100;
    private static final int CLIENT_CONNECTION_TIMEOUT_MS = 10_000;

    private static Logger logger;

    private static void setLogger(Logger logger) {
        TorrentServer.logger = logger;
    }

    public static Logger getLogger() {
        return TorrentServer.logger;
    }

    private final int port;

    public TorrentServer(int port) {
        this.port = port;
    }

    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            String listeningMsg = "Listening on " + this.port + "...";
            TorrentServer.getLogger().log(Level.INFO, LocalDateTime.now(), listeningMsg);
            System.out.println(listeningMsg);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(CLIENT_CONNECTION_TIMEOUT_MS);

                String msg = "Accepted connection request from client " +
                    clientSocket.getInetAddress().getHostAddress() +
                    ":" +
                    clientSocket.getPort();
                TorrentServer.getLogger().log(Level.INFO, LocalDateTime.now(), msg);
                System.out.println(msg);

                ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            TorrentServer.getLogger().log(Level.ERROR, LocalDateTime.now(), writer.toString());
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        if (args.length < ARG_LENGTH) {
            throw new IllegalArgumentException("Invalid arg count!");
        }

        int port = Integer.parseInt(args[ARG_PORT_IDX]);
        String logDir = args[ARG_LOG_DIR_IDX];

        LoggerOptions options = new LoggerOptions(TorrentServer.class, logDir);
        Logger logger = new DefaultLogger(options);
        TorrentServer.setLogger(logger);

        TorrentServer server = new TorrentServer(port);
        server.run();
    }
}
