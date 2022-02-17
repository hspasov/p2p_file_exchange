package bg.sofia.uni.fmi.mjt.torrent.exceptions;

public class InvalidHeaderException extends Exception {
    public InvalidHeaderException(String msg) {
        super(msg);
    }

    public InvalidHeaderException(String msg, Throwable e) {
        super(msg, e);
    }
}
