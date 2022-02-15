package bg.sofia.uni.fmi.mjt.torrent.client.usercommands;

import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.client.TorrentServerRequestor;
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

public class SendToServerCommand extends TorrentServerRequestor implements UserCommand {
    private static final String SERVER_RESPONSE_HEADER_PARTS_SEPARATOR = " ";
    private static final int SERVER_RESPONSE_HEADER_PARTS_COUNT = 2;
    private static final int PART_STATUS_IDX = 0;
    private static final int PART_CONTENT_LENGTH_IDX = 1;

    public SendToServerCommand(String torrentServerAddress, int torrentServerPort) {
        super(torrentServerAddress, torrentServerPort);
    }

    private static int getContentLength(String serverResponse) {
        String[] serverResponseParts = serverResponse.split(SERVER_RESPONSE_HEADER_PARTS_SEPARATOR);
        if (serverResponseParts.length < SERVER_RESPONSE_HEADER_PARTS_COUNT) {
            return 0;
        }
        return Integer.parseInt(serverResponseParts[PART_CONTENT_LENGTH_IDX]);
    }

    @Override
    public void execute(PeerRequest request) throws UserCommandException {
        try (Socket socket = new Socket(this.getTorrentServerAddress(), this.getTorrentServerPort())) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8
            )), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            System.out.println("Updating available peers from torrent server...");
            out.println(request.rawCommand());
            String responseHeader = in.readLine();
            int contentLength = getContentLength(responseHeader);
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
        }
    }
}
