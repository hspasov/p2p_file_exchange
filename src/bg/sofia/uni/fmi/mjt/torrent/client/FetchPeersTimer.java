package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;

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
    // TODO inspect if daemon works properly
    private final long fetchIntervalMs;
    private final String torrentServerAddress;
    private final int torrentServerPort;

    private static TorrentResponse parseListPeersResponse(String response) {
        final String responsePartsSeparator = " ";
        final int statusCodeIdx = 0;
        final int peersCountIdx = 1;
        String[] responseParts = response.split(responsePartsSeparator);
        String statusCode = responseParts[statusCodeIdx];
        int peersCount = Integer.parseInt(responseParts[peersCountIdx]);
        return new TorrentResponse(statusCode.getBytes(StandardCharsets.UTF_8), peersCount);
    }

    private static Peer parsePeerResponse(String input) {
        // TODO validate input
        final String responsePartsSeparator = " ";
        final int usernameIdx = 0;
        final int addressIdx = 1;
        final int portIdx = 2;
        String[] peerParts = input.split(responsePartsSeparator);
        String username = peerParts[usernameIdx];
        String address = peerParts[addressIdx];
        int port = Integer.parseInt(peerParts[portIdx]);
        return new Peer(username, address, port);
    }

    // TODO refactor address and port to -> serverEndpoint
    public FetchPeersTimer(long fetchIntervalMs, String torrentServerAddress, int torrentServerPort) {
        this.fetchIntervalMs = fetchIntervalMs;
        this.torrentServerAddress = torrentServerAddress;
        this.torrentServerPort = torrentServerPort;
    }

    @Override
    public void run() {
        while (true) {
            // TODO if cannot connect to server, load peer data from file
            try (Socket socket = new Socket(this.torrentServerAddress, this.torrentServerPort)) {
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
                TorrentResponse listPeersResponse = parseListPeersResponse(response);

                Map<String, Peer> availablePeers = new HashMap<>();

                // TODO include timeout
                for (int peersRead = 0; peersRead < listPeersResponse.contentLength(); peersRead++) {
                    String peerUnparsed = in.readLine();
                    //System.out.println("Response from server: " + peerUnparsed);
                    Peer peer = parsePeerResponse(peerUnparsed);
                    availablePeers.put(peer.username(), peer);
                }

                PeersAvailabilityInfo peersAvailabilityInfo = PeersAvailabilityInfo.getInstance();
                peersAvailabilityInfo.setAvailablePeers(availablePeers);
                //System.out.println("Peers successfully updated.");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
