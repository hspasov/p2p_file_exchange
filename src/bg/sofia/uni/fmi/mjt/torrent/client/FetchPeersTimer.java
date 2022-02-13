package bg.sofia.uni.fmi.mjt.torrent.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class FetchPeersTimer implements Runnable {
    // TODO inspect if daemon works properly
    private static final long FETCH_INTERVAL_MS = 30_000;
    @Override
    public void run() {
        while (true) {
            try (Socket socket = new Socket(TorrentClient.TORRENT_SERVER_ADDRESS, TorrentClient.TORRENT_SERVER_PORT)) {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8
                )), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
                System.out.println("Updating available peers from torrent server...");
                out.println("list-peers");
                String response = in.readLine();
                System.out.println("Peers successfully updated.");
                System.out.println("Response from server: " + response);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(FETCH_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
