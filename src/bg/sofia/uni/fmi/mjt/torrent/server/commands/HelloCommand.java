package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.Peer;

public class HelloCommand implements TorrentCommand {
    private static final String PAYLOAD_PARTS_SEPARATOR = ":";
    private static final int PAYLOAD_ADDRESS_IDX = 0;
    private static final int PAYLOAD_PORT_IDX = 1;

    @Override
    public TorrentResponse execute(PeerRequest request) throws TorrentRequestException {
        if (request.username() == null) {
            throw new TorrentRequestException("Missing user in unregister command!");
        }
        if (request.payload() == null) {
            throw new TorrentRequestException("Missing payload in unregister command!");
        }

        String[] payloadParts = request.payload().split(PAYLOAD_PARTS_SEPARATOR);
        String address = payloadParts[PAYLOAD_ADDRESS_IDX];
        String port = payloadParts[PAYLOAD_PORT_IDX];

        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setPeerAvailable(new Peer(request.username(), address, Integer.parseInt(port)));
        // TODO fix these hardcoded status codes
        return new TorrentResponse("0");
    }
}
