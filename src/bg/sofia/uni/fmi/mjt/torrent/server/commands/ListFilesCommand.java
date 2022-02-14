package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;

import java.util.Map;
import java.util.Set;

public class ListFilesCommand implements TorrentCommand {
    private static final String ENTRY_SEPARATOR = "\n";
    private static final String USER_FILEPATH_SEPARATOR = " : ";

    @Override
    public TorrentResponse execute(PeerRequest request) {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        Map<String, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getAvailableFiles();
        StringBuilder response = new StringBuilder("0");

        for (var entry : usersAvailableFiles.entrySet()) {
            String username = entry.getKey();
            Set<TorrentFile> files = entry.getValue();
            for (var fileEntry : files) {
                response.append(ENTRY_SEPARATOR)
                    .append(username)
                    .append(USER_FILEPATH_SEPARATOR)
                    .append(fileEntry.filePath());
            }
        }
        return new TorrentResponse(response.toString());
    }
}
