package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListFilesCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListPeersCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.RegistrationCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.TorrentServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ClientRequest;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.TorrentServerResponse;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ClientRequestError;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.UnregistrationCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientRequestHandler implements Runnable {
    private final Socket socket;

    public ClientRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8
            )), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            )
        ) {
            String inputLine = in.readLine();
            System.out.println("Message received from client: " + inputLine);
            ClientRequest request = new ClientRequest(
                inputLine,
                socket.getInetAddress().getHostAddress(),
                socket.getPort()
            );

            TorrentServerCommand torrentServerCommand = switch (request.command()) {
                case "register" -> new RegistrationCommand();
                case "unregister" -> new UnregistrationCommand();
                case "list-files" -> new ListFilesCommand();
                case "list-peers" -> new ListPeersCommand();
                default -> unknownCommand -> new TorrentServerResponse("1");
            };
            TorrentServerResponse response = torrentServerCommand.execute(request);
            out.println(response.toString());
        } catch (IOException | ClientRequestError e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
