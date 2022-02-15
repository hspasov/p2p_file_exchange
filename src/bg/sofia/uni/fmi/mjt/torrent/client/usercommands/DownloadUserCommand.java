package bg.sofia.uni.fmi.mjt.torrent.client.usercommands;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.client.PeersAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.client.TorrentClient;
import bg.sofia.uni.fmi.mjt.torrent.client.TorrentServerRequestor;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class DownloadUserCommand extends TorrentServerRequestor implements UserCommand {
    private static final String PAYLOAD_PARTS_SEPARATOR = " ";
    private static final int PAYLOAD_PARTS_COUNT = 3;
    private static final int PAYLOAD_SRC_IDX = 0;
    private static final int PAYLOAD_DEST_IDX = 1;

    private static final String RESPONSE_HEADER_PARTS_SEPARATOR = " ";
    private static final int RESPONSE_HEADER_PARTS_COUNT = 2;
    private static final int RESPONSE_HEADER_STATUS_IDX = 0;
    private static final int RESPONSE_HEADER_CONTENT_LENGTH_IDX = 1;

    private final Peer self;

    public DownloadUserCommand(Peer self, String torrentServerAddress, int torrentServerPort) {
        super(torrentServerAddress, torrentServerPort);
        this.self = self;
    }

    private static String readResponseHeader(InputStream inputStream) throws IOException {
        final String endOfHeaderMark = "\n";
        StringBuilder result = new StringBuilder();

        while (true) {
            byte[] binarySymbol = inputStream.readNBytes(1);
            String character = new String(binarySymbol, StandardCharsets.UTF_8);

            if (character.equals(endOfHeaderMark)) {
                return result.toString();
            }

            result.append(character);
        }
    }

    @Override
    public void execute(PeerRequest request) throws UserCommandException {
        String[] payloadParts = request.payload().split(PAYLOAD_PARTS_SEPARATOR, PAYLOAD_PARTS_COUNT);
        String peerUsername = request.username();
        String src = payloadParts[PAYLOAD_SRC_IDX];
        String dest = payloadParts[PAYLOAD_DEST_IDX];
        String commandToPeer = String.join(" ", request.command(), peerUsername, src);

        PeersAvailabilityInfo peersAvailabilityInfo = PeersAvailabilityInfo.getInstance();
        Peer peer = peersAvailabilityInfo.getPeer(peerUsername);

        if (peer == null) {
            throw new UserCommandException("Peer does not exist!");
        }

        byte[] file = null;
        try (Socket socket = new Socket(peer.address(), peer.port())) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8
            )), true);
            InputStream inputStream = socket.getInputStream();
            out.println(commandToPeer);
            String responseHeader = readResponseHeader(inputStream);
            String[] responseHeaderParts = responseHeader.split(
                RESPONSE_HEADER_PARTS_SEPARATOR, RESPONSE_HEADER_PARTS_COUNT);

            int contentLength = Integer.parseInt(responseHeaderParts[RESPONSE_HEADER_CONTENT_LENGTH_IDX]);
            System.out.println("content length expected: " + contentLength);
            file = inputStream.readNBytes(contentLength);
            System.out.println("file size: " + file.length);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file == null) {
            // TODO decide what to do in this situation
            return;
        }

        boolean isFileWritten = false;

        File downloadedFile = new File(dest);
        try (FileOutputStream outputStream = new FileOutputStream(downloadedFile)) {
            outputStream.write(file);
            outputStream.flush();
            isFileWritten = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!isFileWritten) {
            return;
        }

        String commandName = "register";
        String payload = dest;
        try {
            PeerRequest registerRequest = new PeerRequest(String.join(" ", commandName, this.self.username(), payload));
            SendToServerCommand command = new SendToServerCommand(this.getTorrentServerAddress(), this.getTorrentServerPort());
            command.execute(registerRequest);
        } catch (TorrentRequestException e) {
            e.printStackTrace();
        }
    }
}
