package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.DownloadUserCommand;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.SendToServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.UserCommand;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;

public class UserInputHandler implements Runnable {
    private final Peer self;
    private final PeerRequest request;
    private final ServerEndpoint serverEndpoint;

    public UserInputHandler(Peer self, PeerRequest request, ServerEndpoint serverEndpoint) {
        this.self = self;
        this.request = request;
        this.serverEndpoint = serverEndpoint;
    }

    @Override
    public void run() {
        UserCommand command = switch (request.command()) {
            case "register", "unregister", "list-files" -> new SendToServerCommand(this.serverEndpoint);
            case "download" -> new DownloadUserCommand(this.self, this.serverEndpoint);
            default -> invalidCommand -> {
                throw new UserCommandException("Invalid command!");
            };
        };
        try {
            command.execute(this.request);
        } catch (UserCommandException e) {
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            TorrentClient.getLogger().log(Level.ERROR, LocalDateTime.now(), writer.toString());
            System.out.println(e);
        }
    }
}
