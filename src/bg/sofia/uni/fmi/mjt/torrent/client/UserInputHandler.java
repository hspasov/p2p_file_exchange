package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.DownloadUserCommand;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.SendToServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.UserCommand;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

public class UserInputHandler implements Runnable {
    private final Peer self;
    private final PeerRequest request;
    private final String torrentServerAddress;
    private final int torrentServerPort;

    public UserInputHandler(Peer self, PeerRequest request, String torrentServerAddress, int torrentServerPort) {
        this.self = self;
        this.request = request;
        this.torrentServerAddress = torrentServerAddress;
        this.torrentServerPort = torrentServerPort;
    }

    @Override
    public void run() {
        UserCommand command = switch (request.command()) {
            case "register", "unregister", "list-files" -> new SendToServerCommand(this.torrentServerAddress, this.torrentServerPort);
            case "download" -> new DownloadUserCommand(this.self, this.torrentServerAddress, this.torrentServerPort);
            default -> invalidCommand -> {
                throw new UserCommandException("Invalid command!");
            };
        };
        try {
            command.execute(this.request);
        } catch (UserCommandException e) {
            e.printStackTrace();
        }
    }
}
