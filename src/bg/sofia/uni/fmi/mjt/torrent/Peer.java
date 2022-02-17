package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.InvalidPeerException;

public record Peer(String username, String address, int port) {
    public static Peer of(String input) throws InvalidPeerException  {
        final String partsSeparator = " ";
        final int expectedPartsCount = 3;
        final int usernameIdx = 0;
        final int addressIdx = 1;
        final int portIdx = 2;
        String[] peerParts = input.split(partsSeparator);

        if (peerParts.length != expectedPartsCount) {
            throw new InvalidPeerException("Cannot create peer from invalid input string!");
        }

        String username = peerParts[usernameIdx];
        String address = peerParts[addressIdx];

        int port;
        try {
            port = Integer.parseInt(peerParts[portIdx]);
        } catch (NumberFormatException e) {
            throw new InvalidPeerException("Cannot create peer: port is not a valid number!", e);
        }

        return new Peer(username, address, port);
    }
}
