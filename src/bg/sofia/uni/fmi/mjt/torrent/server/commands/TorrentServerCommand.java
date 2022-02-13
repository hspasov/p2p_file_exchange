package bg.sofia.uni.fmi.mjt.torrent.server.commands;

public interface TorrentServerCommand {
    TorrentServerResponse execute(ClientRequest request) throws ClientRequestError;
}
