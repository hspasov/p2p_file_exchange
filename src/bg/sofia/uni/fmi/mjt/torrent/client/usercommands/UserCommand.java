package bg.sofia.uni.fmi.mjt.torrent.client.usercommands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

public interface UserCommand {
    void execute(PeerRequest request) throws UserCommandException;
}