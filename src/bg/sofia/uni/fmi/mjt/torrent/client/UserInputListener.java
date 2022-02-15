package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.client.usercommands.SendToServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserInputListener {
    private static final int MAX_EXECUTOR_THREADS = 100;
    private Peer self;
    private final String torrentServerAddress;
    private final int torrentServerPort;

    public UserInputListener(Peer self, String torrentServerAddress, int torrentServerPort) {
        this.self = self;
        this.torrentServerAddress = torrentServerAddress;
        this.torrentServerPort = torrentServerPort;
    }

    private void sendHelloToServer(String username) {
        try {
            String commandName = "hello";
            String payload = this.self.address() + ":" + this.self.port();
            PeerRequest request = new PeerRequest(String.join(" ", commandName, username, payload));
            SendToServerCommand command = new SendToServerCommand(this.torrentServerAddress, this.torrentServerPort);
            command.execute(request);
        } catch (TorrentRequestException e) {
            // TODO this should be app error
            e.printStackTrace();
        } catch (UserCommandException e) {
            e.printStackTrace();
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
            // TODO validate all commands - forbid extra space, forbid space in usernames
            System.out.print("TorrentClient=# ");
            String command = scanner.nextLine();
            try {
                PeerRequest request = new PeerRequest(command);
                this.identifyOneself(request.username());
                UserInputHandler userInputHandler = new UserInputHandler(this.self, request, this.torrentServerAddress, this.torrentServerPort);
                executor.execute(userInputHandler);
            } catch (TorrentRequestException e) {
                e.printStackTrace();
            }
        }
    }
}
