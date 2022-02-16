package bg.sofia.uni.fmi.mjt.torrent.exceptions;

public class TorrentRequestException extends Exception {
    public TorrentRequestException(String msg) {
        super(msg);
    }

    public TorrentRequestException(String msg, Throwable e) {
        super(msg, e);
    }
}
