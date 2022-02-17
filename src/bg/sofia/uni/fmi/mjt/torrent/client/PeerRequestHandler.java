package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentCommand;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.client.commands.DownloadCommand;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.HelloCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListFilesCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListPeersCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.RegistrationCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.UnregistrationCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
            OutputStream out = socket.getOutputStream();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            )
        ) {
            String inputLine = in.readLine();
            System.out.println("Message received from peer: " + inputLine);
            PeerRequest request = new PeerRequest(inputLine);

            TorrentCommand torrentCommand = PeerRequestHandler.get(request.command());
            TorrentResponse response = torrentCommand.execute(request);

            out.write(response.getContent());
            out.flush();
        } catch (IOException | TorrentRequestException e) {
            System.out.println(e.getMessage());
        } finally {
            // TODO check if should be closed
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static TorrentCommand get(String command) {
        return switch (command) {
            case DownloadCommand.COMMAND_NAME -> new DownloadCommand();
            default -> unknownCommand -> new TorrentResponse(
                TorrentResponse.getFailureHeader().getBytes(StandardCharsets.UTF_8)
            );
        };
    }
}
