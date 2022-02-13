package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListFilesCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.RegistrationCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.TorrentServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ClientRequest;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ClientRequestError;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.TorrentServerResponse;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.UnregistrationCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PeerRequestHandler implements Runnable {
    private final Socket socket;

    public PeerRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            Writer out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
            );
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            )
        ) {
            String inputLine = in.readLine();
            System.out.println("Message received from peer: " + inputLine);
            ClientRequest request = new ClientRequest(
                inputLine,
                socket.getInetAddress().getHostAddress(),
                socket.getPort()
            );

            TorrentServerCommand torrentServerCommand = switch (request.command()) {
                case "register" -> new RegistrationCommand();
                case "unregister" -> new UnregistrationCommand();
                case "list-files" -> new ListFilesCommand();
                default -> unknownCommand -> new TorrentServerResponse("1");
            };
            TorrentServerResponse response = torrentServerCommand.execute(request);
            out.write(response.toString());
            out.flush();
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
