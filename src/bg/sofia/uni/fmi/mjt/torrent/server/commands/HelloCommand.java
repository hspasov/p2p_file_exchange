package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.Peer;

import java.nio.charset.StandardCharsets;

public class HelloCommand implements TorrentCommand {
    private static final String PAYLOAD_PARTS_SEPARATOR = ":";
    private static final int PAYLOAD_ADDRESS_IDX = 0;
    private static final int PAYLOAD_PORT_IDX = 1;

    @Override
    public TorrentResponse execute(PeerRequest request) throws TorrentRequestException {
        if (request.username() == null) {
            // TODO maybe this should be return TorrentResponse 1?
            throw new TorrentRequestException("Missing user in hello command!");
        }
        if (request.payload() == null) {
            throw new TorrentRequestException("Missing payload in hello command!");
        }

        String[] payloadParts = request.payload().split(PAYLOAD_PARTS_SEPARATOR);
        String address = payloadParts[PAYLOAD_ADDRESS_IDX];
        int port;
        try {
            port = Integer.parseInt(payloadParts[PAYLOAD_PORT_IDX]);
        } catch (NumberFormatException e) {
            throw new TorrentRequestException("Invalid port in hello command!", e);
        }

        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(new Peer(request.username(), address, port));
        // TODO fix these hardcoded status codes
        return new TorrentResponse("0\n".getBytes(StandardCharsets.UTF_8));
    }
}
