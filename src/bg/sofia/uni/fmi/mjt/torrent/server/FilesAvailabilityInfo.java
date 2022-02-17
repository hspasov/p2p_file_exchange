package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.torrent.Peer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilesAvailabilityInfo {
    // TODO lock only specific user in set methods
    private Map<String, Peer> availablePeers;
    private Map<String, Set<TorrentFile>> usersAvailableFiles;
    private static final FilesAvailabilityInfo instance = new FilesAvailabilityInfo();

    private FilesAvailabilityInfo() {
        this.availablePeers = new HashMap<>();
        this.usersAvailableFiles = new HashMap<>();
    }

    public static FilesAvailabilityInfo getInstance() {
        return FilesAvailabilityInfo.instance;
    }

    public synchronized void setPeerAvailable(Peer peer) {
        this.availablePeers.put(peer.username(), peer);
        this.usersAvailableFiles.putIfAbsent(peer.username(), new HashSet<>());
    }

    public synchronized void setFilesAvailable(String username, Set<TorrentFile> files) {
        Set<TorrentFile> userAvailableFiles = this.usersAvailableFiles.get(username);
        if (userAvailableFiles == null) {
            return;
        }
        userAvailableFiles.addAll(files);
    }

    public synchronized void setFilesUnavailable(String username, Set<TorrentFile> files) {
        Set<TorrentFile> userAvailableFiles = this.usersAvailableFiles.get(username);
        if (userAvailableFiles == null) {
            return;
        }
        userAvailableFiles.removeAll(files);
    }

    public synchronized void reset() {
        this.availablePeers = new HashMap<>();
        this.usersAvailableFiles = new HashMap<>();
    }

    public synchronized Map<String, Peer> getAvailablePeers() {
        return new HashMap<>(this.availablePeers);
    }

    public synchronized Map<String, Set<TorrentFile>> getAvailableFiles() {
        return this.usersAvailableFiles.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue())));
    }
}
