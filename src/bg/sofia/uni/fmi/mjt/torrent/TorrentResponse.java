package bg.sofia.uni.fmi.mjt.torrent;

public class TorrentResponse {
    private final byte[] content;
    private final Integer contentLength;

    public TorrentResponse(byte[] content) {
        this.content = content;
        this.contentLength = null;
    }

    public TorrentResponse(byte[] content, int contentLength) {
        this.content = content;
        this.contentLength = contentLength;
    }

    public Integer contentLength() {
        return this.contentLength;
    }

    public byte[] getContent() {
        return this.content;
    }
}
