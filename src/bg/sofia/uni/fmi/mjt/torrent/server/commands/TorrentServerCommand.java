package bg.sofia.uni.fmi.mjt.torrent.server.commands;

public interface TorrentServerCommand {
    String COMMAND_PARTS_SEPARATOR = " ";
    String FILES_SEPARATOR = ", ";
    int COMMAND_PARTS_COUNT = 3;
    int NAME_IDX = 0;
    int USER_IDX = 1;
    int FILES_IDX = 2;

    TorrentServerCommandResponse execute(String command);
}
