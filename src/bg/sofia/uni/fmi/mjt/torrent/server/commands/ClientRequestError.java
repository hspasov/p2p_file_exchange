package bg.sofia.uni.fmi.mjt.torrent.server.commands;

public class ClientRequestError extends Exception {
    public ClientRequestError(String msg) {
        super(msg);
    }
}
