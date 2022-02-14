package bg.sofia.uni.fmi.mjt.torrent;

public class TorrentResponse {
    private final String content;
    private final Integer contentLength;

    public TorrentResponse(String content) {
        this.content = content;
        this.contentLength = null;
    }

    public TorrentResponse(String content, int contentLength) {
        this.content = content;
        this.contentLength = contentLength;
    }

    public Integer contentLength() {
        return this.contentLength;
    }

    @Override
    public String toString() {
        return this.content;
    }
}
