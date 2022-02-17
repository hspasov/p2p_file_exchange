package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.Peer;
import bg.sofia.uni.fmi.mjt.torrent.server.FilesAvailabilityInfo;
import org.junit.jupiter.api.BeforeEach;

public abstract class TorrentCommandTest {
    protected static final String COMMAND_PARTS_SEPARATOR = " ";
    protected static final Peer TEST_PEER1 = new Peer("test-peer1", "0.0.0.0", 4000);
    protected static final Peer TEST_PEER2 = new Peer("test-peer2", "0.0.0.0", 4001);
    protected static final String TEST_ADDRESS = "0.0.0.0";
    protected static final String TEST_FILE1 = "/path/to/file1";
    protected static final String TEST_FILE2 = "/path/to/file2";

    @BeforeEach
    protected void setUp() {
        FilesAvailabilityInfo filesAvailabilityInfo = FilesAvailabilityInfo.getInstance();
        filesAvailabilityInfo.reset();
    }
}
