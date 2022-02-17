package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RegistrationCommand implements TorrentCommand {
    public static final String COMMAND_NAME = "register";
    public static final String FILES_SEPARATOR = ", ";

    @Override
    public TorrentResponse execute(PeerRequest request) throws TorrentRequestException {
        if (request.username() == null) {
            // TODO throw TorrentRequestNoUsernameException
            throw new TorrentRequestException("Missing user in register command!");
        }
        if (request.payload() == null) {
            throw new TorrentRequestException("Missing payload in register command!");
        }

        Set<TorrentFile> files = Arrays.stream(request.payload().split(FILES_SEPARATOR))
            .map(TorrentFile::new)
            .collect(Collectors.toSet());
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.setFilesAvailable(request.username(), files);
        return new TorrentResponse(TorrentResponse.getSuccessHeader().getBytes(StandardCharsets.UTF_8));
    }
}
