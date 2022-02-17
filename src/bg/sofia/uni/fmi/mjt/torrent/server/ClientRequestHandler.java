package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.DownloadCommand;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.HelloCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListFilesCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListPeersCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.RegistrationCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.UnregistrationCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
            OutputStream out = socket.getOutputStream();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            )
        ) {
            String inputLine = in.readLine();
            System.out.println("Message received from client: " + inputLine);
            PeerRequest request = new PeerRequest(inputLine);

            TorrentCommand torrentCommand = ClientRequestHandler.get(request.command());
            TorrentResponse response = torrentCommand.execute(request);
            out.write(response.getContent());
        } catch (IOException | TorrentRequestException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static TorrentCommand get(String command) {
        return switch (command) {
            case HelloCommand.COMMAND_NAME -> new HelloCommand();
            case RegistrationCommand.COMMAND_NAME -> new RegistrationCommand();
            case UnregistrationCommand.COMMAND_NAME -> new UnregistrationCommand();
            case ListFilesCommand.COMMAND_NAME -> new ListFilesCommand();
            case ListPeersCommand.COMMAND_NAME -> new ListPeersCommand();
            default -> unknownCommand -> new TorrentResponse(
                TorrentResponse.getFailureHeader().getBytes(StandardCharsets.UTF_8)
            );
        };
    }
}
