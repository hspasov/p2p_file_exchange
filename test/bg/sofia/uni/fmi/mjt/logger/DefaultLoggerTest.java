package bg.sofia.uni.fmi.mjt.logger;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultLoggerTest {

    @Test
    void testLog() throws IOException {
        LoggerOptions options = mock(LoggerOptions.class);

        String dirPath = "./log";
        File dirFile = new File(dirPath);

        if (dirFile.exists() && dirFile.isDirectory()) {
            for (File file : dirFile.listFiles()) {
                file.delete();
            }
        }

        when(options.getDirectory()).thenReturn(dirPath);
        when(options.getMaxFileSizeBytes()).thenReturn(1024L);
        when(options.getMinLogLevel()).thenReturn(Level.DEBUG);
        doReturn(DefaultLoggerTest.class).when(options).getClazz();

        LocalDateTime timestamp = LocalDateTime.of(2021,11,27,1,11,3,935601200);
        String msg = "Test message123";
        Level level = Level.DEBUG;

        Logger logger = new DefaultLogger(options);
        logger.log(level, timestamp, msg);

        String allContent = Files.readString(logger.getCurrentFilePath());

        assertEquals("[DEBUG]|2021-11-27T01:11:03.935601200|bg.sofia.uni.fmi.mjt.logger|Test message123" + System.lineSeparator(), allContent);

        for (File file : dirFile.listFiles()) {
            file.delete();
        }
    }

    @Test
    void testTwoLinesLog() throws IOException {
        LoggerOptions options = mock(LoggerOptions.class);

        String dirPath = "./log";
        File dirFile = new File(dirPath);

        if (dirFile.exists() && dirFile.isDirectory()) {
            for (File file : dirFile.listFiles()) {
                file.delete();
            }
        }

        when(options.getDirectory()).thenReturn(dirPath);
        when(options.getMinLogLevel()).thenReturn(Level.DEBUG);
        when(options.getMaxFileSizeBytes()).thenReturn(1024L);
        doReturn(DefaultLoggerTest.class).when(options).getClazz();

        LocalDateTime timestamp1 = LocalDateTime.of(2021,10,12,6,5,4,300000111);
        LocalDateTime timestamp2 = LocalDateTime.of(2021,10,12,6,5,5,203101319);

        String msg = "Test message123";
        String msg2 = "Another test message";
        Level level1 = Level.ERROR;
        Level level2 = Level.WARN;

        Logger logger = new DefaultLogger(options);
        logger.log(level1, timestamp1, msg);
        logger.log(level2, timestamp2, msg2);

        String allContent = Files.readString(logger.getCurrentFilePath());
        String line1 = "[ERROR]|2021-10-12T06:05:04.300000111|bg.sofia.uni.fmi.mjt.logger|Test message123" + System.lineSeparator();
        String line2 = "[WARN]|2021-10-12T06:05:05.203101319|bg.sofia.uni.fmi.mjt.logger|Another test message" + System.lineSeparator();

        assertEquals( line1 + line2, allContent);

        for (File file : dirFile.listFiles()) {
            file.delete();
        }
    }

    @Test
    void getCurrentFilePath() {
        LoggerOptions options = mock(LoggerOptions.class);
        String dirPath = "./log";
        File dirFile = new File(dirPath);

        if (dirFile.exists() && dirFile.isDirectory()) {
            for (File file : dirFile.listFiles()) {
                file.delete();
            }
        }

        when(options.getDirectory()).thenReturn(dirPath);
        when(options.getMinLogLevel()).thenReturn(Level.DEBUG);
        when(options.getMaxFileSizeBytes()).thenReturn(100L);
        doReturn(DefaultLoggerTest.class).when(options).getClazz();

        Logger logger = new DefaultLogger(options);

        logger.log(Level.DEBUG,LocalDateTime.now(),"test");
        logger.log(Level.DEBUG,LocalDateTime.now(),"test");

        Path file1 = Path.of("./log/logs-0.txt");

        assertEquals(file1, logger.getCurrentFilePath());

        logger.log(Level.DEBUG,LocalDateTime.now(),"test");

        Path file2 = Path.of("./log/logs-1.txt");
        assertEquals(file2, logger.getCurrentFilePath());

        for (File file : dirFile.listFiles()) {
            file.delete();
        }
    }
}