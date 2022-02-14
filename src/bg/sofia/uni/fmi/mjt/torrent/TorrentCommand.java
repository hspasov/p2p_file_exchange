package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;

public interface TorrentCommand {
    TorrentResponse execute(PeerRequest request) throws TorrentRequestException;
}
