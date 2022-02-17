package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadCommand implements TorrentCommand {
    public static final String COMMAND_NAME = "download";

    @Override
    public TorrentResponse execute(PeerRequest request) throws TorrentRequestException {
        if (request.payload() == null) {
            throw new TorrentRequestException("Missing payload in download command!");
        }
        String fileSrc = request.payload();

        try {
            byte[] file = Files.readAllBytes(Path.of(fileSrc));
            String header = TorrentResponse.getSuccessHeader(file.length);
            byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
            byte[] response = new byte[headerBytes.length + file.length];
            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(file, 0, response, headerBytes.length, file.length);
            return new TorrentResponse(response);
        } catch (IOException e) {
            return new TorrentResponse(TorrentResponse.getFailureHeader().getBytes(StandardCharsets.UTF_8));
        }
    }
}
