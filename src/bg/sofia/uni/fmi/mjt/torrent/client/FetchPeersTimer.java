package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.InvalidHeaderException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.InvalidPeerException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FetchPeersTimer implements Runnable {
    private static final int SERVER_CONNECTION_TIMEOUT_MS = 10_000;

    private final long fetchIntervalMs;
    private final ServerEndpoint serverEndpoint;

    public FetchPeersTimer(long fetchIntervalMs, ServerEndpoint serverEndpoint) {
        this.fetchIntervalMs = fetchIntervalMs;
        this.serverEndpoint = serverEndpoint;
    }

    @Override
    public void run() {
        while (true) {
            try (Socket socket = new Socket(this.serverEndpoint.address(), this.serverEndpoint.port())) {
                socket.setSoTimeout(SERVER_CONNECTION_TIMEOUT_MS);
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8
                )), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
                TorrentClient.getLogger().log(
                    Level.INFO,
                    LocalDateTime.now(),
                    "Updating available peers from torrent server..."
                );
                out.println("list-peers");
                String response = in.readLine();
                TorrentClient.getLogger().log(
                    Level.INFO,
                    LocalDateTime.now(),
                    "Response from server: " + response
                );
                int peersCount = TorrentResponse.getContentLength(response);

                Map<String, Peer> availablePeers = new HashMap<>();

                for (int peersRead = 0; peersRead < peersCount; peersRead++) {
                    String peerUnparsed = in.readLine();
                    TorrentClient.getLogger().log(
                        Level.INFO,
                        LocalDateTime.now(),
                        "Response from server: " + peerUnparsed
                    );
                    Peer peer = Peer.of(peerUnparsed);
                    availablePeers.put(peer.username(), peer);
                }

                PeersAvailabilityInfo peersAvailabilityInfo = PeersAvailabilityInfo.getInstance();
                peersAvailabilityInfo.setAvailablePeers(availablePeers);
                TorrentClient.getLogger().log(
                    Level.INFO,
                    LocalDateTime.now(),
                    "Peers successfully updated."
                );
            } catch (IOException | InvalidPeerException | InvalidHeaderException e) {
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                TorrentClient.getLogger().log(Level.ERROR, LocalDateTime.now(), writer.toString());
            }

            try {
                Thread.sleep(this.fetchIntervalMs);
            } catch (InterruptedException e) {
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                TorrentClient.getLogger().log(Level.ERROR, LocalDateTime.now(), writer.toString());
            }
        }
    }
}
