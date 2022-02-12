package bg.sofia.uni.fmi.mjt.torrent.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilesAvailabilityInfo {
    // TODO lock only specific user in set methods
    private final Map<User, Set<TorrentFile>> usersAvailableFiles;
    private static final FilesAvailabilityInfo instance = new FilesAvailabilityInfo();

    private FilesAvailabilityInfo() {
        this.usersAvailableFiles = new HashMap<>();
    }

    public static FilesAvailabilityInfo getInstance() {
        return FilesAvailabilityInfo.instance;
    }

    public synchronized void setFilesAvailable(User user, Set<TorrentFile> files) {
        this.usersAvailableFiles.putIfAbsent(user, new HashSet<>());
        this.usersAvailableFiles.get(user).addAll(files);
    }

    public synchronized void setFilesUnavailable(User user, Set<TorrentFile> files) {
        Set<TorrentFile> providedFiles = this.usersAvailableFiles.get(user);
        if (providedFiles == null) {
            return;
        }
        providedFiles.removeAll(files);
        if (providedFiles.isEmpty()) {
            this.usersAvailableFiles.remove(user);
        }
    }

    public synchronized Map<User, Set<TorrentFile>> getFilesAvailable() {
        return this.usersAvailableFiles.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue())));
    }
}
