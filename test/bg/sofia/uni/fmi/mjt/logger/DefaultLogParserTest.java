package bg.sofia.uni.fmi.mjt.logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultLogParserTest {
    private static LocalDateTime timestamp1 = LocalDateTime.of(2021,10,12,6,5,4,300000111);
    private static LocalDateTime timestamp2 = LocalDateTime.of(2021,10,12,6,5,5,203101319);
    private static LocalDateTime timestamp3 = LocalDateTime.of(2021,10,12,6,5,5,203101320);
    private static LocalDateTime timestamp4 = LocalDateTime.of(2021,10,12,6,5,5,203101321);
    private static LocalDateTime timestamp5 = LocalDateTime.of(2021,10,12,6,5,5,203101322);

    private static String msg1 = "Test msg1";
    private static String msg2 = "Test msg2";
    private static String msg3 = "Test msg3";
    private static String msg4 = "Test msg4";
    private static String msg5 = "Test msg5";

    private static Level level1 = Level.ERROR;
    private static Level level2 = Level.INFO;
    private static Level level3 = Level.ERROR;
    private static Level level4 = Level.WARN;
    private static Level level5 = Level.ERROR;

    private static String dirPath = "./log";
    private static Path path = Path.of(dirPath);
    private static String expectedPackageName = "bg.sofia.uni.fmi.mjt.logger";

    @BeforeAll
    static void createLog() {
        LoggerOptions options = mock(LoggerOptions.class);

        File dirFile = new File(dirPath);

        if (dirFile.exists() && dirFile.isDirectory()) {
            for (File file : dirFile.listFiles()) {
                file.delete();
            }
        }

        when(options.getDirectory()).thenReturn(dirPath);
        when(options.getMinLogLevel()).thenReturn(Level.DEBUG);
        doReturn(DefaultLogParserTest.class).when(options).getClazz();

        Logger logger = new DefaultLogger(options);

        logger.log(level1, timestamp1, msg1);
        logger.log(level2, timestamp2, msg2);
        logger.log(level3, timestamp3, msg3);
        logger.log(level4, timestamp4, msg4);
        logger.log(level5, timestamp5, msg5);
    }

    @Test
    void testGetLogs() {
        List<Log> expectedErrorLogs = List.of(
            new Log(level1, timestamp1, expectedPackageName, msg1),
            new Log(level3, timestamp3, expectedPackageName, msg3),
            new Log(level5, timestamp5, expectedPackageName, msg5)
        );

        List<Log> expectedWarnLogs = List.of(
            new Log(level4, timestamp4, expectedPackageName, msg4)
        );

        LogParser parser = new DefaultLogParser(path);
        List<Log> errorLogs = parser.getLogs(Level.ERROR);
        List<Log> warnLogs = parser.getLogs(Level.WARN);

        assertIterableEquals(expectedErrorLogs, errorLogs);
        assertIterableEquals(expectedWarnLogs, warnLogs);
    }

    @Test
    void testGetLogsEmpty() {
        List<Log> expectedLogs = new ArrayList<>();

        LogParser parser = new DefaultLogParser(path);
        List<Log> logs = parser.getLogs(Level.DEBUG);

        assertIterableEquals(expectedLogs, logs);
    }

    @Test
    void testGetLogsThrows() {
        LogParser parser = new DefaultLogParser(path);
        assertThrows(IllegalArgumentException.class, () -> parser.getLogs(null));
    }

    @Test
    void testGetLogsTimestamp() {
        List<Log> expectedLogs = List.of(
            new Log(level1, timestamp1, expectedPackageName, msg1),
            new Log(level2, timestamp2, expectedPackageName, msg2),
            new Log(level3, timestamp3, expectedPackageName, msg3)
        );

        LogParser parser = new DefaultLogParser(path);
        List<Log> logs = parser.getLogs(timestamp1, timestamp4);

        assertIterableEquals(expectedLogs, logs);
    }

    @Test
    void testGetLogsTimestampEmpty() {
        List<Log> expectedLogs = new ArrayList<>();

        LogParser parser = new DefaultLogParser(path);

        LocalDateTime laterTimestamp1 = LocalDateTime.of(2022, 1, 1, 1, 1,1);
        LocalDateTime laterTimestamp2 = LocalDateTime.of(2022, 1, 1, 1, 1,2);

        assertIterableEquals(expectedLogs, parser.getLogs(laterTimestamp1, laterTimestamp2));
        assertIterableEquals(expectedLogs, parser.getLogs(timestamp4, timestamp1));
    }

    @Test
    void testGetLogsTimestampThrows() {
        LogParser parser = new DefaultLogParser(path);

        assertThrows(IllegalArgumentException.class, () -> parser.getLogs(null, LocalDateTime.now()));
        assertThrows(IllegalArgumentException.class, () -> parser.getLogs(LocalDateTime.now(), null));
        assertThrows(IllegalArgumentException.class, () -> parser.getLogs(null, null));
    }

    @Test
    void testGetLogsTail() {
        List<Log> expectedLogs = List.of(
            new Log(level4, timestamp4, expectedPackageName, msg4),
            new Log(level5, timestamp5, expectedPackageName, msg5)
        );
        LogParser parser = new DefaultLogParser(path);

        assertIterableEquals(expectedLogs, parser.getLogsTail(2));
    }

    @Test
    void testGetLogsTailZero() {
        List<Log> expectedLogs = new ArrayList<>();
        LogParser parser = new DefaultLogParser(path);

        assertIterableEquals(expectedLogs, parser.getLogsTail(0));
    }

    @Test
    void testGetLogsTailAllLog() {
        List<Log> expectedLogs = List.of(
            new Log(level1, timestamp1, expectedPackageName, msg1),
            new Log(level2, timestamp2, expectedPackageName, msg2),
            new Log(level3, timestamp3, expectedPackageName, msg3),
            new Log(level4, timestamp4, expectedPackageName, msg4),
            new Log(level5, timestamp5, expectedPackageName, msg5)
        );
        LogParser parser = new DefaultLogParser(path);

        assertIterableEquals(expectedLogs, parser.getLogsTail(1000));
    }

    @Test
    void testGetLogsTailThrows() {
        LogParser parser = new DefaultLogParser(path);

        assertThrows(IllegalArgumentException.class, () -> parser.getLogsTail(-1));
    }

    @AfterAll
    static void deleteLog() {
        File dirFile = new File(dirPath);

        for (File file : dirFile.listFiles()) {
            file.delete();
        }
    }
}