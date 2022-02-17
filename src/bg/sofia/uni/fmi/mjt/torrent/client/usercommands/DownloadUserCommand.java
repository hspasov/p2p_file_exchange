package bg.sofia.uni.fmi.mjt.torrent.client.usercommands;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.PeerRequest;
import bg.sofia.uni.fmi.mjt.torrent.TorrentResponse;
import bg.sofia.uni.fmi.mjt.torrent.client.PeersAvailabilityInfo;
import bg.sofia.uni.fmi.mjt.torrent.client.ServerEndpoint;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.InvalidHeaderException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.TorrentRequestException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.UserCommandException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DownloadUserCommand implements UserCommand {
    private static final String PAYLOAD_PARTS_SEPARATOR = " ";
    private static final int PAYLOAD_PARTS_COUNT = 3;
    private static final int PAYLOAD_SRC_IDX = 0;
    private static final int PAYLOAD_DEST_IDX = 1;

    private static final String RESPONSE_HEADER_PARTS_SEPARATOR = " ";
    private static final int RESPONSE_HEADER_PARTS_COUNT = 2;
    private static final int RESPONSE_HEADER_STATUS_IDX = 0;
    private static final int RESPONSE_HEADER_CONTENT_LENGTH_IDX = 1;

    private final ServerEndpoint serverEndpoint;
    private final Peer self;

    public DownloadUserCommand(Peer self, ServerEndpoint serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
        this.self = self;
    }

    private static byte[] receiveFileFromPeer(Peer peer, String commandToPeer)
        throws IOException, InvalidHeaderException {
        try (Socket socket = new Socket(peer.address(), peer.port())) {
            socket.setSoTimeout(SERVER_CONNECTION_TIMEOUT_MS);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8
            )), true);
            InputStream inputStream = socket.getInputStream();
            out.println(commandToPeer);
            String responseHeader = TorrentResponse.readResponseHeader(inputStream);
            int contentLength = TorrentResponse.getContentLength(responseHeader);
            return inputStream.readNBytes(contentLength);
        }
    }

    private static void writeToFileSystem(byte[] file, String dest) throws IOException {
        File downloadedFile = new File(dest);
        try (FileOutputStream outputStream = new FileOutputStream(downloadedFile)) {
            outputStream.write(file);
            outputStream.flush();
        }
    }

    private void sendRegisterToServer(String filePath) throws UserCommandException, TorrentRequestException {
        PeerRequest registerRequest = new PeerRequest(String.join(
            PAYLOAD_PARTS_SEPARATOR, "register", this.self.username(), filePath
        ));
        SendToServerCommand command = new SendToServerCommand(this.serverEndpoint);
        command.execute(registerRequest);
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

        byte[] file;
        try {
            file = receiveFileFromPeer(peer, commandToPeer);
        } catch (IOException | InvalidHeaderException e) {
            throw new UserCommandException("Failed to receive file from peer!", e);
        }

        try {
            writeToFileSystem(file, dest);
        } catch (IOException e) {
            throw new UserCommandException("Failed to write file to file system!", e);
        }

        try {
            this.sendRegisterToServer(dest);
        } catch (TorrentRequestException e) {
            throw new UserCommandException("Failed to send register command to server!", e);
        }
    }
}
