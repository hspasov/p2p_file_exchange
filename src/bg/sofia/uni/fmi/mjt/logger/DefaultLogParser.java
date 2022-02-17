package bg.sofia.uni.fmi.mjt.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class DefaultLogParser implements LogParser {
    private final Path logsFilePath;

    public DefaultLogParser(Path logsFilePath) {
        this.logsFilePath = logsFilePath;
    }

    private List<Log> readLogFile(File file) {
        List<Log> result = new ArrayList<>();

        try (BufferedReader fileReader = Files.newBufferedReader(file.toPath())) {
            final int splitLimit = 4;
            String line;

            while ((line = fileReader.readLine()) != null) {
                String[] lineSplit = line.split("\\|", splitLimit);
                final int levelIdx = 0;
                final int timestampIdx = 1;
                final int packageNameIdx = 2;
                final int msgIdx = 3;

                Level level = switch (lineSplit[levelIdx]) {
                    case "[ERROR]" -> Level.ERROR;
                    case "[WARN]" -> Level.WARN;
                    case "[INFO]" -> Level.INFO;
                    case "[DEBUG]" -> Level.DEBUG;
                    default -> throw new IllegalStateException();
                };

                LocalDateTime timestamp = LocalDateTime.parse(lineSplit[timestampIdx]);
                String packageName = lineSplit[packageNameIdx];
                String message = lineSplit[msgIdx];

                result.add(new Log(level, timestamp, packageName, message));
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }

        return result;
    }

    private List<Log> getAllLogs() {
        File logsDir = new File(this.logsFilePath.toString());

        List<Log> allLogs = new ArrayList<>();

        TreeSet<File> logFiles = new TreeSet<>(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String file1NumStr = o1.getName().replaceAll(DefaultLogger.LOG_FILE_PREFIX, "")
                    .replaceAll(DefaultLogger.LOG_FILE_SUFFIX, "");
                String file2NumStr = o2.getName().replaceAll(DefaultLogger.LOG_FILE_PREFIX, "")
                    .replaceAll(DefaultLogger.LOG_FILE_SUFFIX, "");

                int file1Num = Integer.parseInt(file1NumStr);
                int file2Num = Integer.parseInt(file2NumStr);

                return Integer.compare(file1Num, file2Num);
            }
        });

        Collections.addAll(logFiles, logsDir.listFiles());

        for (File file : logFiles) {
            allLogs.addAll(this.readLogFile(file));
        }

        return allLogs;
    }

    @Override
    public List<Log> getLogs(Level level) {
        if (level == null) {
            throw new IllegalArgumentException();
        }

        List<Log> allLogs = this.getAllLogs();
        List<Log> result = new ArrayList<>();

        for (Log log : allLogs) {
            if (log.level() == level) {
                result.add(log);
            }
        }

        return result;
    }

    @Override
    public List<Log> getLogs(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException();
        }

        List<Log> allLogs = this.getAllLogs();
        List<Log> result = new ArrayList<>();

        for (Log log : allLogs) {
            LocalDateTime timestamp = log.timestamp();
            if ((timestamp.isEqual(from) || timestamp.isAfter(from)) && timestamp.isBefore(to)) {
                result.add(log);
            }
        }

        return result;
    }

    @Override
    public List<Log> getLogsTail(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        List<Log> allLogs = this.getAllLogs();

        return allLogs.subList(Math.max(0, allLogs.size() - n), allLogs.size());
    }
}
