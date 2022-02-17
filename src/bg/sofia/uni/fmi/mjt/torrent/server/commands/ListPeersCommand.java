package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.Peer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ListPeersCommand implements TorrentCommand {
    public static final String COMMAND_NAME = "list-peers";
    public static final String ENTRY_SEPARATOR = "\n";
    public static final String PEER_PROPERTY_SEPARATOR = " ";

    @Override
    public TorrentResponse execute(PeerRequest request) {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        Map<String, Peer> availablePeers = filesAvailabilityInfo.getAvailablePeers();
        StringBuilder response = new StringBuilder("0 " + availablePeers.size() + "\n");

        for (var entry : availablePeers.entrySet()) {
            String username = entry.getKey();
            Peer peer = entry.getValue();

            response.append(username)
                .append(PEER_PROPERTY_SEPARATOR)
                .append(peer.address())
                .append(PEER_PROPERTY_SEPARATOR)
                .append(peer.port())
                .append(ENTRY_SEPARATOR);
        }
        return new TorrentResponse(response.toString().getBytes(StandardCharsets.UTF_8));
    }
}
