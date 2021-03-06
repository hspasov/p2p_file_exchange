package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class ListFilesCommand implements TorrentCommand {
    public static final String COMMAND_NAME = "list-files";
    public static final String ENTRY_SEPARATOR = "\n";
    public static final String USER_FILEPATH_SEPARATOR = " : ";

    @Override
    public TorrentResponse execute(PeerRequest request) {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        Map<String, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getAvailableFiles();
        StringBuilder content = new StringBuilder();
        int contentSize = 0;
        for (var entry : usersAvailableFiles.entrySet()) {
            String username = entry.getKey();
            Set<TorrentFile> files = entry.getValue();
            for (var fileEntry : files) {
                contentSize++;
                content.append(username)
                    .append(USER_FILEPATH_SEPARATOR)
                    .append(fileEntry.filePath())
                    .append(ENTRY_SEPARATOR);
            }
        }
        StringBuilder response = new StringBuilder(TorrentResponse.getSuccessHeader(contentSize));
        response.append(content);
        return new TorrentResponse(response.toString().getBytes(StandardCharsets.UTF_8));
    }
}
