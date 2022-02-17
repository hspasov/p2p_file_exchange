package bg.sofia.uni.fmi.mjt.torrent.client;

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
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FetchPeersTimer implements Runnable {
    private static final int SERVER_CONNECTION_TIMEOUT_MS = 10_000;

    // TODO inspect if daemon works properly
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
                //System.out.println("Updating available peers from torrent server...");
                out.println("list-peers");
                String response = in.readLine();
                //System.out.println("Response from server: " + response);
                int peersCount = TorrentResponse.getContentLength(response);

                Map<String, Peer> availablePeers = new HashMap<>();

                for (int peersRead = 0; peersRead < peersCount; peersRead++) {
                    String peerUnparsed = in.readLine();
                    //System.out.println("Response from server: " + peerUnparsed);
                    Peer peer = Peer.of(peerUnparsed);
                    availablePeers.put(peer.username(), peer);
                }

                PeersAvailabilityInfo peersAvailabilityInfo = PeersAvailabilityInfo.getInstance();
                peersAvailabilityInfo.setAvailablePeers(availablePeers);
                //System.out.println("Peers successfully updated.");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidPeerException e) {
                e.printStackTrace();
            } catch (InvalidHeaderException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(this.fetchIntervalMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
