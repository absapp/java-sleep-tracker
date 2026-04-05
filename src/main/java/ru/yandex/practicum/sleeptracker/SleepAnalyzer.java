package ru.yandex.practicum.sleeptracker;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum SleepAnalyzer implements SleepFunction {

    TOTAL_SESSIONS {
        @Override
        public String analyze(List<SleepingSession> sessions) {
            return "Всего сессий: " + sessions.size();
        }
    },

    SHORTEST_SLEEP {
        @Override
        public String analyze(List<SleepingSession> sessions) {
            return sessions.stream()
                    .min(Comparator.comparing(SleepingSession::getDuration))
                    .map(session -> {
                        long hours = session.getDuration().toHours();
                        long minutes = session.getDuration().toMinutesPart();
                        return "Самая короткая сессия: " + hours + "ч " + minutes + "мин";
                    })
                    .orElse("Нет данных");
        }
    },

    LONGEST_SLEEP {
        @Override
        public String analyze(List<SleepingSession> sessions) {
            return sessions.stream()
                    .max(Comparator.comparing(SleepingSession::getDuration))
                    .map(session -> {
                        long hours = session.getDuration().toHours();
                        long minutes = session.getDuration().toMinutesPart();
                        return "Самая долгая сессия: " + hours + "ч " + minutes + "мин";
                    })
                    .orElse("Нет данных");
        }
    },

    AVERAGE_DURATION {
        @Override
        public String analyze(List<SleepingSession> sessions) {
            double avgMinutes = sessions.stream()
                    .mapToLong(s -> s.getDuration().toMinutes())
                    .average()
                    .orElse(0);
            long hours = (long) avgMinutes / 60;
            long minutes = Math.round(avgMinutes % 60);
            return "Средняя длительность сна: " + hours + "ч " + minutes + "мин";
        }
    },

    BAD_SESSIONS {
        @Override
        public String analyze(List<SleepingSession> sessions) {
            long count = sessions.stream()
                    .filter(s -> s.getSleepQuality() == SleepQuality.BAD)
                    .count();
            return "Плохой сон (BAD): " + count;
        }
    },

    UNSLEEP_NIGHT {
        @Override
        public String analyze(List<SleepingSession> sessions) {
            long count = sessions.stream()
                    .filter(s -> (s.isUnSleepDay()))
                    .count();
            return "Бессонных ночей " + count;
        }
    },

    CHRONOTYPE {
        @Override
        public String analyze(List<SleepingSession> sessions) {
            Map<Chronotype, Long> counts = sessions.stream()
                    .filter(s -> s.isNightSleep())
                    .map(SleepingSession::getChronotype)
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

            if (counts.isEmpty()) {
                return "Недостаточно данных для определения хронотипа";
            }

            Chronotype result = counts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(Chronotype.DOVE);

            return "Ваш хронотип: " + result.getDescription();
        }
    }
}
