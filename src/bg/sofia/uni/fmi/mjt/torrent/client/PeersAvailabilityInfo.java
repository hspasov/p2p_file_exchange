package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;
import bg.sofia.uni.fmi.mjt.torrent.Peer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PeersAvailabilityInfo {
    private Map<String, Peer> availablePeers;
    private String peersFile;

    private static final String PEER_ADDRESS_SEPARATOR = " - ";
    private static final String ADDRESS_PORT_SEPARATOR = ":";
    private static final int PEERS_FILE_PARSE_COMPONENTS_COUNT = 2;
    private static final int PEERS_FILE_PEER_NAME_IDX = 0;
    private static final int PEERS_FILE_LOCATION_IDX = 1;
    private static final int LOCATION_ADDRESS_IDX = 0;
    private static final int LOCATION_PORT_IDX = 1;
    private static PeersAvailabilityInfo instance;

    private PeersAvailabilityInfo(String peersFile) {
        this.peersFile = peersFile;
        this.availablePeers = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.peersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] peerAndLocation = line.split(PEER_ADDRESS_SEPARATOR);
                if (peerAndLocation.length != PEERS_FILE_PARSE_COMPONENTS_COUNT) {
                    continue;
                }
                String peerName = peerAndLocation[PEERS_FILE_PEER_NAME_IDX];
                String location = peerAndLocation[PEERS_FILE_LOCATION_IDX];

                String[] addressAndPort = location.split(ADDRESS_PORT_SEPARATOR);
                if (addressAndPort.length != PEERS_FILE_PARSE_COMPONENTS_COUNT) {
                    continue;
                }

                String address = addressAndPort[LOCATION_ADDRESS_IDX];
                int port;
                try {
                    port = Integer.parseInt(addressAndPort[LOCATION_PORT_IDX]);
                } catch (NumberFormatException e) {
                    continue;
                }

                this.availablePeers.put(peerName, new Peer(peerName, address, port));
            }
        } catch (IOException e) {
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            Logger logger = TorrentClient.getLogger();
            if (logger != null) {
                logger.log(Level.ERROR, LocalDateTime.now(), writer.toString());
            }
        }
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

    public synchronized void setAvailablePeers(Map<String, Peer> availablePeers) throws IOException {
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
        }
    }

    public synchronized Peer getPeer(String username) {
        return this.availablePeers.get(username);
    }
}
