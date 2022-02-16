package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;

public class PeerRequest {
    public static final String COMMAND_PARTS_SEPARATOR = " ";

    public static final int COMMAND_PARTS_COUNT = 3;
    public static final int COMMAND_NAME_IDX = 0;
    public static final int USER_IDX = 1;
    public static final int PAYLOAD_IDX = 2;

    private final String command;
    private final String username;
    private final String payload;
    private final String rawCommand;

    public PeerRequest(String command) throws TorrentRequestException {
        if (command == null) {
            throw new TorrentRequestException("Invalid command!");
        }

        String[] commandParts = command.split(COMMAND_PARTS_SEPARATOR, COMMAND_PARTS_COUNT);
        this.command = commandParts[COMMAND_NAME_IDX];

        if (commandParts.length > USER_IDX) {
            this.username = commandParts[USER_IDX];
        } else {
            this.username = null;
        }

        if (commandParts.length > PAYLOAD_IDX) {
            this.payload = commandParts[PAYLOAD_IDX];
        } else {
            this.payload = null;
        }

        this.rawCommand = command;
    }

    public String command() {
        return this.command;
    }

    public String username() {
        return this.username;
    }

    public String payload() {
        return this.payload;
    }

    public String rawCommand() {
        return this.rawCommand;
    }
}
