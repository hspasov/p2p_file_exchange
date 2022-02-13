package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;
import bg.sofia.uni.fmi.mjt.torrent.server.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientRequest {
    public static final String COMMAND_PARTS_SEPARATOR = " ";
    public static final String FILES_SEPARATOR = ", ";

    public static final int COMMAND_PARTS_COUNT = 3;
    public static final int COMMAND_NAME_IDX = 0;
    public static final int USER_IDX = 1;
    public static final int FILES_IDX = 2;

    private final String command;
    private final User user;
    private final Set<TorrentFile> files;

    public ClientRequest(String command, String remoteIPAddr, int remotePort) throws ClientRequestError {
        if (command == null) {
            throw new ClientRequestError("Invalid command!");
        }

        String[] commandParts = command.split(COMMAND_PARTS_SEPARATOR, COMMAND_PARTS_COUNT);

        if (commandParts.length <= COMMAND_NAME_IDX) {
            throw new ClientRequestError("Missing command name!");
        }
        this.command = commandParts[COMMAND_NAME_IDX];

        if (commandParts.length > USER_IDX) {
            this.user = new User(commandParts[USER_IDX], remoteIPAddr, remotePort);
        } else {
            this.user = null;
        }

        if (commandParts.length > FILES_IDX) {
            this.files = Arrays.stream(commandParts[FILES_IDX].split(FILES_SEPARATOR))
                .map(TorrentFile::new)
                .collect(Collectors.toSet());
        } else {
            this.files = new HashSet<>();
        }
    }

    public String command() {
        return this.command;
    }

    public User user() {
        return this.user;
    }

    public Set<TorrentFile> files() {
        return this.files;
    }
}
