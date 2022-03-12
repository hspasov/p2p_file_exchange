package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.Peer;

import java.nio.charset.StandardCharsets;

public class HelloCommand implements TorrentCommand {
    public static final String COMMAND_NAME = "hello";
    public static final String PAYLOAD_PARTS_SEPARATOR = ":";
    private static final int PAYLOAD_ADDRESS_IDX = 0;
    private static final int PAYLOAD_PORT_IDX = 1;
    private static final int PAYLOAD_PARTS_COUNT = 2;

    @Override
    public TorrentResponse execute(PeerRequest request) throws TorrentRequestException {
        if (request.username() == null) {
            throw new TorrentRequestException("Missing user in hello command!");
        }
        if (request.payload() == null) {
            throw new TorrentRequestException("Missing payload in hello command!");
        }

        String[] payloadParts = request.payload().split(PAYLOAD_PARTS_SEPARATOR, PAYLOAD_PARTS_COUNT);

        if (payloadParts.length != PAYLOAD_PARTS_COUNT) {
            throw new TorrentRequestException("Hello command expected address and port to be provided!");
        }

        String address = payloadParts[PAYLOAD_ADDRESS_IDX];
        int port;
        try {
            port = Integer.parseInt(payloadParts[PAYLOAD_PORT_IDX]);
        } catch (NumberFormatException e) {
            throw new TorrentRequestException("Invalid port in hello command!", e);
        }

        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(new Peer(request.username(), address, port));
        return new TorrentResponse(TorrentResponse.getSuccessHeader().getBytes(StandardCharsets.UTF_8));
    }
}
