package bg.sofia.uni.fmi.mjt.torrent.exceptions;

public class InvalidPeerException extends Exception {
    public InvalidPeerException(String msg) {
        super(msg);
    }

    public InvalidPeerException(String msg, Throwable e) {
        super(msg, e);
    }
}
