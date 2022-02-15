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
    @Override
    public TorrentResponse execute(PeerRequest request) throws TorrentRequestException {
        if (request.payload() == null) {
            throw new TorrentRequestException("Missing payload in download command!");
        }
        String fileSrc = request.payload();

        try {
            // TODO read in chunks
            byte[] file = Files.readAllBytes(Path.of(fileSrc));
            String header = "0 " + file.length + "\n";
            byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
            byte[] response = new byte[headerBytes.length + file.length];
            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(file, 0, response, headerBytes.length, file.length);
            return new TorrentResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            return new TorrentResponse("1\n".getBytes(StandardCharsets.UTF_8));
        }
    }
}
