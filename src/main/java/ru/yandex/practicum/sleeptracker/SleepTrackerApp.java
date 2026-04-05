package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SleepTrackerApp {

    public static void main(String[] args) {

        try {
            Logger logger = new Logger(Path.of("sleep_tracker.log"));
            SleepingSessionLoader loader = new SleepingSessionLoader("src/main/resources/sleep_log.txt", logger);
            List<SleepingSession> sessions = loader.getSleepingSessions();

            if (sessions.isEmpty()) {
                System.out.println("Нет данных о сне");
                return;
            }

            System.out.println("=== Анализ сна ===\n");

            for (SleepAnalyzer analyzer : SleepAnalyzer.values()) {
                System.out.println(analyzer.analyze(sessions));
            }

        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
