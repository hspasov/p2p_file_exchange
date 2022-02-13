package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;

public class RegistrationCommand implements TorrentServerCommand {
    @Override
    public TorrentServerResponse execute(ClientRequest request) throws ClientRequestError {
        if (request.user() == null) {
            throw new ClientRequestError("Missing user in unregister command!");
        }

        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setFilesAvailable(request.user(), request.files());
        return new TorrentServerResponse("0");
    }
}
