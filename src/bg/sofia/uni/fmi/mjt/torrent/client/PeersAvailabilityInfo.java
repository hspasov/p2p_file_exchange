package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PeersAvailabilityInfo {
    private Map<String, Peer> availablePeers;
    private String peersFile;

    private static final String PEER_ADDRESS_SEPARATOR = " - ";
    private static final String ADDRESS_PORT_SEPARATOR = ":";
    private static PeersAvailabilityInfo instance;

    private PeersAvailabilityInfo(String peersFile) {
        this.availablePeers = new HashMap<>();
        this.peersFile = peersFile;
    }

    public static void setPeersFile(String peersFile) {
        PeersAvailabilityInfo.instance = new PeersAvailabilityInfo(peersFile);
    }

    public static PeersAvailabilityInfo getInstance() {
        if (PeersAvailabilityInfo.instance == null) {
            throw new IllegalStateException("PeersAvailabilityInfo not initialized!");
        }
        return PeersAvailabilityInfo.instance;
    }

    public synchronized void setAvailablePeers(Map<String, Peer> availablePeers) {
        this.availablePeers = new HashMap<>(availablePeers);

        try (FileOutputStream out = new FileOutputStream(this.peersFile)) {
            for (var entry : this.availablePeers.entrySet()) {
                String username = entry.getKey();
                Peer peer = entry.getValue();

                String peerEntry = username +
                    PEER_ADDRESS_SEPARATOR +
                    peer.address() +
                    ADDRESS_PORT_SEPARATOR +
                    peer.port() +
                    System.lineSeparator();

                out.write(peerEntry.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }

            //System.out.println(this.peersFile + " written successfully on disk.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Peer getPeer(String username) {
        return this.availablePeers.get(username);
    }
}
