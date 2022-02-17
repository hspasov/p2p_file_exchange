package bg.sofia.uni.fmi.mjt.logger;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.TreeSet;

public class DefaultLogger implements Logger {
    public static final String LOG_FILE_PREFIX = "logs-";
    public static final String LOG_FILE_SUFFIX = ".txt";
    private final LoggerOptions options;

    public DefaultLogger(LoggerOptions options) {
        this.options = options;
    }

    @Override
    public void log(Level level, LocalDateTime timestamp, String message) {
        if (level == null || timestamp == null || message == null || message.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (level.getLevel() < this.options.getMinLogLevel().getLevel()) {
            return;
        }

        Path logFile = this.getCurrentFilePath();
        File file = logFile.toFile();

        if (file.length() > this.options.getMaxFileSizeBytes()) {
            try {
                this.createNewLogFile();
            } catch (IOException e) {
                if (options.shouldThrowErrors()) {
                    throw new LogException();
                }
            }
            logFile = this.getCurrentFilePath();
            file = logFile.toFile();
        }

        String line = "[" +
            level.toString() +
            "]|" +
            timestamp.toString() +
            "|" +
            this.options.getClazz().getPackageName() +
            "|" +
            message +
            System.lineSeparator();

        try {
            Files.createDirectories(logFile.getParent());
        } catch (IOException e) {
            if (options.shouldThrowErrors()) {
                throw new LogException();
            }
        }

        try (
            Writer fileWriter = Files.newBufferedWriter(
                logFile,
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
            )) {
            fileWriter.append(line);
            fileWriter.flush();
        } catch (IOException e) {
            if (options.shouldThrowErrors()) {
                throw new LogException();
            }
        }
    }

    @Override
    public LoggerOptions getOptions() {
        return this.options;
    }

    @Override
    public Path getCurrentFilePath() {
        final String firstLogFileIdx = "0";
        String firstLogFile = DefaultLogger.LOG_FILE_PREFIX + firstLogFileIdx + DefaultLogger.LOG_FILE_SUFFIX;

        File dirFile = new File(this.options.getDirectory());

        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return Paths.get(this.options.getDirectory(), firstLogFile);
        }

        TreeSet<String> filenames = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String file1NumStr = o1.replaceAll(DefaultLogger.LOG_FILE_PREFIX, "")
                    .replaceAll(DefaultLogger.LOG_FILE_SUFFIX, "");
                String file2NumStr = o2.replaceAll(DefaultLogger.LOG_FILE_PREFIX, "")
                    .replaceAll(DefaultLogger.LOG_FILE_SUFFIX, "");

                int file1Num = Integer.parseInt(file1NumStr);
                int file2Num = Integer.parseInt(file2NumStr);

                return Integer.compare(file1Num, file2Num);
            }
        });

        File[] dirContents = dirFile.listFiles();

        if (dirContents != null) {
            for (File file : dirContents) {
                String fileName = file.getName();

                if (!fileName.matches(DefaultLogger.LOG_FILE_PREFIX + "[0-9]+" + DefaultLogger.LOG_FILE_SUFFIX)) {
                    continue;
                }

                filenames.add(fileName);
            }
        }

        if (filenames.size() == 0) {
            filenames.add(firstLogFile);
        }

        return Paths.get(this.options.getDirectory(), filenames.last());
    }

    private void createNewLogFile() throws IOException {
        Path currentLogFile = this.getCurrentFilePath();
        Path parent = currentLogFile.getParent();
        String fileName = currentLogFile.getFileName().toString();
        String logNumberStr = fileName.replaceAll(DefaultLogger.LOG_FILE_PREFIX, "")
            .replaceAll(DefaultLogger.LOG_FILE_SUFFIX, "");
        int logNumber = Integer.parseInt(logNumberStr);
        int newLogNumber = logNumber + 1;
        String newFileName = DefaultLogger.LOG_FILE_PREFIX + newLogNumber + DefaultLogger.LOG_FILE_SUFFIX;
        Path newLogFilePath = Path.of(parent.toString(), newFileName);
        File newLogFile = newLogFilePath.toFile();
        newLogFile.createNewFile();
    }
}
