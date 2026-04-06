package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class SleepTrackerApp {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Ошибка: не указан путь к файлу с логом сна");
            System.err.println("Использование: java SleepTrackerApp <путь_к_файлу>");
            System.err.println("Пример: java SleepTrackerApp src/main/resources/sleep_log.txt");
            return;
        }

        String filePath = args[0];

        try {
            Logger logger = new Logger(Path.of("sleep_tracker.log"));
            SleepingSessionLoader loader = new SleepingSessionLoader(filePath, logger);
            List<SleepingSession> sessions = loader.getSleepingSessions();

            if (sessions.isEmpty()) {
                System.out.println("Нет данных о сне");
                return;
            }

            System.out.println("=== Анализ сна ===\n");

            Arrays.stream(SleepAnalyzer.values())
                    .map(analyzer -> analyzer.analyze(sessions))
                    .forEach(result -> {
                        System.out.println(result);
                        System.out.println("----------------------------------------");
                    });

        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}

