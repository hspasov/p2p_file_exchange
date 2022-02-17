package bg.sofia.uni.fmi.mjt.torrent.exceptions;

public class UserCommandException extends Exception {
    public UserCommandException(String msg) {
        super(msg);
    }

    public UserCommandException(String msg, Throwable e) {
        super(msg, e);
    }
}
