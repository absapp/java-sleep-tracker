package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum SleepAnalyzer implements SleepFunction {

    TOTAL_SESSIONS {
        @Override
        public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
            long count = sessions.size();
            return new SleepAnalysisResult<>("Всего сессий", count);
        }
    },

    SHORTEST_SLEEP {
        @Override
        public SleepAnalysisResult<Duration> analyze(List<SleepingSession> sessions) {
            Duration shortest = sessions.stream()
                    .map(SleepingSession::getDuration)
                    .min(Duration::compareTo)
                    .orElse(null);
            return new SleepAnalysisResult<>("Самая короткая сессия", shortest);
        }
    },

    LONGEST_SLEEP {
        @Override
        public SleepAnalysisResult<Duration> analyze(List<SleepingSession> sessions) {
            Duration longest = sessions.stream()
                    .map(SleepingSession::getDuration)
                    .max(Duration::compareTo)
                    .orElse(null);
            return new SleepAnalysisResult<>("Самая долгая сессия", longest);
        }
    },

    AVERAGE_DURATION {
        @Override
        public SleepAnalysisResult<Double> analyze(List<SleepingSession> sessions) {
            double avgMinutes = sessions.stream()
                    .mapToLong(s -> s.getDuration().toMinutes())
                    .average()
                    .orElse(0);
            return new SleepAnalysisResult<>("Средняя длительность сна (мин)", avgMinutes);
        }
    },

    BAD_SESSIONS {
        @Override
        public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
            long count = sessions.stream()
                    .filter(s -> s.getSleepQuality() == SleepQuality.BAD)
                    .count();
            return new SleepAnalysisResult<>("Плохой сон (BAD)", count);
        }
    },

    UNSLEEP_NIGHT {
        @Override
        public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
            long count = sessions.stream()
                    .filter(SleepingSession::isUnSleepDay)
                    .count();
            return new SleepAnalysisResult<>("Бессонных ночей", count);
        }
    },

    CHRONOTYPE {
        @Override
        public SleepAnalysisResult<Chronotype> analyze(List<SleepingSession> sessions) {
            Map<Chronotype, Long> counts = sessions.stream()
                    .filter(SleepingSession::isNightSleep)
                    .map(SleepingSession::getChronotype)
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

            if (counts.isEmpty()) {
                return new SleepAnalysisResult<>("Хронотип", null);
            }

            Chronotype result = counts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(Chronotype.DOVE);

            return new SleepAnalysisResult<>("Ваш хронотип", result);
        }
    };
}
