package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;
import bg.sofia.uni.fmi.mjt.torrent.server.User;

import java.util.Map;
import java.util.Set;

public class ListFilesCommand implements TorrentServerCommand {
    private static final String ENTRY_SEPARATOR = "\n";
    private static final String USER_FILEPATH_SEPARATOR = " : ";

    @Override
    public TorrentServerCommandResponse execute(String command) {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        Map<User, Set<TorrentFile>> usersAvailableFiles = filesAvailabilityInfo.getFilesAvailable();
        StringBuilder response = new StringBuilder("0");
        for (var entry : usersAvailableFiles.entrySet()) {
            User user = entry.getKey();
            Set<TorrentFile> files = entry.getValue();
            for (var fileEntry : files) {
                response.append(ENTRY_SEPARATOR)
                    .append(user.username())
                    .append(USER_FILEPATH_SEPARATOR)
                    .append(fileEntry.filePath());
            }
        }
        return new TorrentServerCommandResponse(response.toString());
    }
}
