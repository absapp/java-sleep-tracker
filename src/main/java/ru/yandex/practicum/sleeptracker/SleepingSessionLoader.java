package ru.yandex.practicum.sleeptracker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleepingSessionLoader {
    private final Path logFile;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private final Logger logger;

    public SleepingSessionLoader(String pathFile, Logger  logger) {
        this.logFile = Paths.get(pathFile);
        this.logger = logger;
    }

    public List<SleepingSession> getSleepingSessions() throws IOException {
        if (!Files.exists(logFile)) {
            throw new FileNotFoundException("Файл не найден: " + logFile.toString());

        }

        try (Stream<String> sleepAnalyses = Files.lines(logFile)) {
            return sleepAnalyses
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.split(";"))
                    .filter(parts -> parts.length == 3)
                    .map(parts -> {
                        String startTime = parts[0].trim();
                        String endTime = parts[1].trim();
                        String status = parts[2].trim();
                        Optional<LocalDateTime> startOpt = parseDateTime(startTime);
                        Optional<LocalDateTime> endOpt = parseDateTime(endTime);

                        if (startOpt.isEmpty() || endOpt.isEmpty()) {
                            logger.log("Пропущена запись с null-значениями");
                            return null;
                        }

                        LocalDateTime start = startOpt.get();
                        LocalDateTime end = endOpt.get();

                        SleepQuality quality = parseSleepQuality(status);
                        if (quality == SleepQuality.INDETERMINATE) {
                            logger.log("Неизвестное качество сна: '" + status +
                                    "' в записи: " + startTime + ";" + endTime + ";" + status);
                        }

                        return new SleepingSession(start, end, quality);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

    }

    private SleepQuality parseSleepQuality(String status) {
        try {
            return SleepQuality.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SleepQuality.INDETERMINATE;
        }
    }

    private Optional<LocalDateTime> parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(LocalDateTime.parse(dateTimeStr, FORMATTER));
        } catch (DateTimeParseException e) {
            logger.log("Ошибка парсинга даты: '" + dateTimeStr + "' — неверный формат. Ожидается: dd.MM.yy HH:mm");
            return Optional.empty();
        }
    }

}
