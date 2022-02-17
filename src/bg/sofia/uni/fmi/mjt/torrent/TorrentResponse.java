package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.InvalidHeaderException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TorrentResponse {
    public static final String HEADER_PARTS_SEPARATOR = " ";
    public static final int HEADER_PARTS_COUNT = 2;
    public static final int PART_CONTENT_LENGTH_IDX = 1;
    private static final String SUCCESS_CODE = "0";
    private static final String FAILURE_CODE = "1";
    private static final String HEADER_END = "\n";

    private final byte[] content;

    public TorrentResponse(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return this.content;
    }

    public static String readResponseHeader(InputStream inputStream) throws IOException, InvalidHeaderException {
        final int maxHeaderBytes = 1024;
        final String endOfHeaderMark = "\n";
        StringBuilder result = new StringBuilder();

        int bytesRead = 0;

        while (bytesRead < maxHeaderBytes) {
            byte[] binarySymbol = inputStream.readNBytes(1);

            if (binarySymbol.length == 0) {
                throw new InvalidHeaderException("End of stream reached before reading the whole header!");
            }

            String character = new String(binarySymbol, StandardCharsets.UTF_8);

            if (character.equals(endOfHeaderMark)) {
                return result.toString();
            }

            result.append(character);
            bytesRead++;
        }

        throw new InvalidHeaderException("Limit reached while reading header!");
    }

    public static int getContentLength(String header) throws InvalidHeaderException {
        String[] headerParts = header.split(HEADER_PARTS_SEPARATOR);
        if (headerParts.length < HEADER_PARTS_COUNT) {
            return 0;
        }
        int contentLength;
        try {
            contentLength = Integer.parseInt(headerParts[PART_CONTENT_LENGTH_IDX]);
            return contentLength;
        } catch (NumberFormatException e) {
            throw new InvalidHeaderException("Content length is not a number!", e);
        }
    }

    public static String getSuccessHeader() {
        return SUCCESS_CODE + HEADER_END;
    }

    public static String getSuccessHeader(int contentLength) {
        return SUCCESS_CODE +
            HEADER_PARTS_SEPARATOR +
            contentLength +
            HEADER_END;
    }

    public static String getFailureHeader() {
        return FAILURE_CODE + HEADER_END;
    }
}
