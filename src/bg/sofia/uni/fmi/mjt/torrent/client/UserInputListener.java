package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.SendToServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.UserCommand;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserInputListener {
    private static final int MAX_EXECUTOR_THREADS = 100;
    private Peer self;
    private final ServerEndpoint serverEndpoint;

    public UserInputListener(Peer self, ServerEndpoint serverEndpoint) {
        this.self = self;
        this.serverEndpoint = serverEndpoint;
    }

    private void sendHelloToServer(String username) {
        try {
            String commandName = "hello";
            String payload = this.self.address() + ":" + this.self.port();
            PeerRequest request = new PeerRequest(String.join(" ", commandName, username, payload));
            SendToServerCommand command = new SendToServerCommand(this.serverEndpoint);
            command.execute(request);
        } catch (TorrentRequestException | UserCommandException e) {
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            TorrentClient.getLogger().log(Level.ERROR, LocalDateTime.now(), writer.toString());
        }
    }

    private void identifyOneself(String username) {
        if (username != null && this.self.username() == null) {
            this.self = new Peer(username, this.self.address(), this.self.port());
            this.sendHelloToServer(username);
        }
    }

    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("TorrentClient=# ");
            String command = scanner.nextLine();

            if (command.strip().length() == 0) {
                continue;
            }

            try {
                PeerRequest request = new PeerRequest(command);
                if (UserCommand.REGISTER_COMMAND.equals(request.command())) {
                    this.identifyOneself(request.username());
                }
                UserInputHandler userInputHandler = new UserInputHandler(this.self, request, this.serverEndpoint);
                executor.execute(userInputHandler);
            } catch (TorrentRequestException e) {
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                TorrentClient.getLogger().log(Level.ERROR, LocalDateTime.now(), writer.toString());
                System.out.println(e.getMessage());
            }
        }
    }
}
