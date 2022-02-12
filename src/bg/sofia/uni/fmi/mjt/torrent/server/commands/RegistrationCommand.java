package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentFile;
import bg.sofia.uni.fmi.mjt.torrent.server.User;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RegistrationCommand implements TorrentServerCommand {
    @Override
    public TorrentServerCommandResponse execute(String command) {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        String[] commandParts = command.split(COMMAND_PARTS_SEPARATOR, COMMAND_PARTS_COUNT);
        User user = new User(commandParts[USER_IDX], "", "");
        Set<TorrentFile> files = Arrays.stream(commandParts[FILES_IDX].split(FILES_SEPARATOR))
            .map(TorrentFile::new)
            .collect(Collectors.toSet());
        filesAvailabilityInfo.setFilesAvailable(user, files);
        return new TorrentServerCommandResponse("0");
    }
}
