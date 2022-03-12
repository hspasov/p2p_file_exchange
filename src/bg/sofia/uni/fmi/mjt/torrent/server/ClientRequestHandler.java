package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.logger.Level;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
            String msg = "Message received from client: " + inputLine;
            TorrentServer.getLogger().log(Level.INFO, LocalDateTime.now(), msg);
            System.out.println(msg);
            try {
                PeerRequest request = new PeerRequest(inputLine);
                TorrentCommand torrentCommand = ClientRequestHandler.get(request.command());
                TorrentResponse response = torrentCommand.execute(request);
                out.write(response.getContent());
            } catch (TorrentRequestException e) {
                out.write(TorrentResponse.getFailureHeader().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            TorrentServer.getLogger().log(Level.ERROR, LocalDateTime.now(), writer.toString());
            System.out.println(e);
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
