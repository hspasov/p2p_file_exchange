package bg.sofia.uni.fmi.mjt.torrent.client.usercommands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.client.ServerEndpoint;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.InvalidHeaderException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SendToServerCommand implements UserCommand {
    private final ServerEndpoint serverEndpoint;

    public SendToServerCommand(ServerEndpoint serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }

    @Override
    public void execute(PeerRequest request) throws UserCommandException {
        try (Socket socket = new Socket(this.serverEndpoint.address(), this.serverEndpoint.port())) {
            socket.setSoTimeout(SERVER_CONNECTION_TIMEOUT_MS);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8
            )), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            out.println(request.rawCommand());
            String responseHeader = in.readLine();
            int contentLength = TorrentResponse.getContentLength(responseHeader);
            if (contentLength == 0) {
                return;
            }
            for (int linesRead = 0; linesRead < contentLength; linesRead++) {
                String responseLine = in.readLine();
                System.out.println(responseLine);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }
    }
}
