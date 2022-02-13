package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.User;

import java.util.Set;

public class ListPeersCommand implements TorrentServerCommand {
    private static final String ENTRY_SEPARATOR = "\n";
    private static final String USER_PROPERTY_SEPARATOR = " ";

    @Override
    public TorrentServerResponse execute(ClientRequest request) {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        Set<User> usersAvailable = filesAvailabilityInfo.getFilesAvailable().keySet();
        StringBuilder response = new StringBuilder("0");

        for (User user : usersAvailable) {
            response.append(ENTRY_SEPARATOR)
                .append(user.username())
                .append(USER_PROPERTY_SEPARATOR)
                .append(user.ipAddress())
                .append(USER_PROPERTY_SEPARATOR)
                .append(user.port());
        }
        return new TorrentServerResponse(response.toString());
    }
}
