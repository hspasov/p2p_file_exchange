package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.torrent.server.commands.ListFilesCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.RegistrationCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.TorrentServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.TorrentServerCommandResponse;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.UnregistrationCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TorrentServerRequestHandler implements Runnable {
    private final Socket socket;

    public TorrentServerRequestHandler(Socket socket) {
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
            System.out.println("Message received from client: " + inputLine);
            String[] lineParts = inputLine.split(TorrentServerCommand.COMMAND_PARTS_SEPARATOR);

            // TODO what if inputLine is empty?
            // TODO what if command should take arguments, but none provided? What if 0 files are provided?
            String command = lineParts[0];

            TorrentServerCommand torrentServerCommand = switch (command) {
                case "register" -> new RegistrationCommand();
                case "unregister" -> new UnregistrationCommand();
                case "list-files" -> new ListFilesCommand();
                default -> unknownCommand -> new TorrentServerCommandResponse("1");
            };
            TorrentServerCommandResponse response = torrentServerCommand.execute(inputLine);
            out.write(response.toString());
            out.flush();
        } catch (IOException e) {
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
