package bg.sofia.uni.fmi.mjt.torrent.client.usercommands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

public interface UserCommand {
    int SERVER_CONNECTION_TIMEOUT_MS = 10_000;
    String REGISTER_COMMAND = "register";
    String UNREGISTER_COMMAND = "unregister";
    String LIST_FILES_COMMAND = "list-files";
    String DOWNLOAD_COMMAND = "download";

    void execute(PeerRequest request) throws UserCommandException;
}