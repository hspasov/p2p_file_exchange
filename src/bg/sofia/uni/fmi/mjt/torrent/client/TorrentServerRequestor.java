package bg.sofia.uni.fmi.mjt.torrent.client;

public abstract class TorrentServerRequestor {
    private final String torrentServerAddress;
    private final int torrentServerPort;

    public TorrentServerRequestor(String torrentServerAddress, int torrentServerPort) {
        this.torrentServerAddress = torrentServerAddress;
        this.torrentServerPort = torrentServerPort;
    }

    public int getTorrentServerPort() {
        return torrentServerPort;
    }

    public String getTorrentServerAddress() {
        return torrentServerAddress;
    }
}
