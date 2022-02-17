package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.InvalidHeaderException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class TorrentResponseTest {

    @Test
    public void testReadResponseHeader() throws IOException, InvalidHeaderException {
        final String header = "0 124";
        final String payload = "payload";
        String content = String.join("\n", header, payload);
        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        String resultHeader = TorrentResponse.readResponseHeader(is);
        assertEquals(header, resultHeader, "Result header does not match expected header!");
    }

    @Test
    public void testReadResponseHeaderTooLong() {
        final int headerLength = 2048;
        String longString = "@".repeat(headerLength);
        InputStream is = new ByteArrayInputStream(longString.getBytes(StandardCharsets.UTF_8));
        assertThrows(
            InvalidHeaderException.class,
            () -> TorrentResponse.readResponseHeader(is),
            "Expected readResponseHeader to throw InvalidHeaderException!"
        );
    }

    @Test
    public void testReadResponseHeaderEndOfStreamReached() {
        final String headerWithoutNewLine = "header without newline";
        InputStream is = new ByteArrayInputStream(headerWithoutNewLine.getBytes(StandardCharsets.UTF_8));
        assertThrows(
            InvalidHeaderException.class,
            () -> TorrentResponse.readResponseHeader(is),
            "Expected readResponseHeader to throw InvalidHeaderException!"
        );
    }

    @Test
    public void testGetContentLengthZero() throws InvalidHeaderException {
        final String header = "0";
        final int expectedContentLength = 0;
        int contentLength = TorrentResponse.getContentLength(header);
        assertEquals(expectedContentLength, contentLength, "Expected content length to be zero!");
    }

    @Test
    public void testGetContentLengthNonZero() throws InvalidHeaderException {
        final String header = "0 128";
        final int expectedContentLength = 128;
        int contentLength = TorrentResponse.getContentLength(header);
        assertEquals(expectedContentLength, contentLength, "Unexpected content length!");
    }

    @Test
    public void testGetContentLengthInvalid() {
        final String header = "0 null";
        assertThrows(
            InvalidHeaderException.class,
            () -> TorrentResponse.getContentLength(header),
            "Expected to throw InvalidHeaderException when given invalid content length!"
        );
    }

    @Test
    public void testGetSuccessHeader() {
        final String expectedSuccessHeader = "0\n";
        assertEquals(
            expectedSuccessHeader,
            TorrentResponse.getSuccessHeader(),
            "Success header does not match!"
        );
    }

    @Test
    public void testGetSuccessHeaderWithContentLength() {
        final int contentLength = 128;
        final String expectedSuccessHeader = "0 128\n";
        assertEquals(
            expectedSuccessHeader,
            TorrentResponse.getSuccessHeader(contentLength),
            "Success header does not match!"
        );
    }

    @Test
    public void testGetFailureHeader() {
        final String expectedFailureHeader = "1\n";
        assertEquals(
            expectedFailureHeader,
            TorrentResponse.getFailureHeader(),
            "Success header does not match!"
        );
    }
}