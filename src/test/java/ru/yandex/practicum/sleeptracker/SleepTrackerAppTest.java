package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SleepTrackerAppTest {

    private List<SleepingSession> sessions;

    @BeforeEach
    void setUp() {
        sessions = new ArrayList<>();
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 1, 23, 0),
                LocalDateTime.of(2025, 4, 2, 8, 0),
                SleepQuality.GOOD
        ));
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 2, 22, 0),
                LocalDateTime.of(2025, 4, 3, 7, 0),
                SleepQuality.NORMAL
        ));
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 3, 1, 0),
                LocalDateTime.of(2025, 4, 3, 5, 0),
                SleepQuality.BAD
        ));
    }

    @Test
    void totalSessionShouldReturnCorrectCount() {
        String result = SleepAnalyzer.TOTAL_SESSIONS.analyze(sessions);
        assertTrue(result.contains("3"));
    }

    @Test
    void totalSessionShouldReturnZero_whenEmptyList() {
        String result = SleepAnalyzer.TOTAL_SESSIONS.analyze(new ArrayList<>());
        assertTrue(result.contains("0"));
    }

    @Test
    void shortestSleepShouldReturnShortestDuration() {
        String result = SleepAnalyzer.SHORTEST_SLEEP.analyze(sessions);
        assertTrue(result.contains("4ч") || result.contains("4ч")); // 01:00 до 05:00 = 4 часа
    }

    @Test
    void shortestSleepShouldReturnNoData_whenEmptyList() {
        String result = SleepAnalyzer.SHORTEST_SLEEP.analyze(new ArrayList<>());
        assertEquals("Нет данных", result);
    }

    @Test
    void longestSleepShouldReturnLongestDuration() {
        String result = SleepAnalyzer.LONGEST_SLEEP.analyze(sessions);
        assertTrue(result.contains("9ч")); // 23:00 до 08:00 = 9 часов
    }

    @Test
    void longestSleepShouldReturnNoData_whenEmptyList() {
        String result = SleepAnalyzer.LONGEST_SLEEP.analyze(new ArrayList<>());
        assertEquals("Нет данных", result);
    }

    @Test
    void averageDurationShouldCalculateCorrectAverage() {
        // 9ч + 9ч + 4ч = 22ч / 3 = 7ч 20мин (округление)
        String result = SleepAnalyzer.AVERAGE_DURATION.analyze(sessions);
        assertTrue(result.contains("7ч") || result.contains("7"));
    }

    @Test
    void averageDurationShouldReturnZero_whenEmptyList() {
        String result = SleepAnalyzer.AVERAGE_DURATION.analyze(new ArrayList<>());
        assertTrue(result.contains("0ч"));
    }


    @Test
    void badSessionShouldCountOnlyBadSessions() {
        String result = SleepAnalyzer.BAD_SESSIONS.analyze(sessions);
        assertTrue(result.contains("1"));
    }

    @Test
    void badSessionShouldReturnZero_whenNoBadSessions() {
        List<SleepingSession> goodSessions = new ArrayList<>();
        goodSessions.add(new SleepingSession(
                LocalDateTime.now(), LocalDateTime.now().plusHours(8), SleepQuality.GOOD
        ));
        String result = SleepAnalyzer.BAD_SESSIONS.analyze(goodSessions);
        assertTrue(result.contains("0"));
    }

    @Test
    void unsleepNightShouldCountOnlyUnsleepDays() {
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 4, 7, 0),
                LocalDateTime.of(2025, 4, 4, 9, 0),
                SleepQuality.NORMAL
        ));
        String result = SleepAnalyzer.UNSLEEP_NIGHT.analyze(sessions);
        assertTrue(result.contains("1"));
    }

    @Test
    void unsleepNightShouldReturnZero_whenNoUnsleepDays() {
        List<SleepingSession> nightSessions = new ArrayList<>();
        nightSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 1, 23, 0),
                LocalDateTime.of(2025, 4, 2, 7, 0),
                SleepQuality.GOOD
        ));
        String result = SleepAnalyzer.UNSLEEP_NIGHT.analyze(nightSessions);
        assertTrue(result.contains("0"));
    }

    @Test
    void unsleepNightShouldNotCountSessionsThatStartBefore6am() {
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 4, 5, 0),
                LocalDateTime.of(2025, 4, 4, 6, 30),
                SleepQuality.NORMAL
        ));
        long countBefore = Long.parseLong(SleepAnalyzer.UNSLEEP_NIGHT.analyze(sessions).replaceAll("\\D+", ""));
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 4, 5, 0),
                LocalDateTime.of(2025, 4, 4, 6, 30),
                SleepQuality.NORMAL
        ));
        long countAfter = Long.parseLong(SleepAnalyzer.UNSLEEP_NIGHT.analyze(sessions).replaceAll("\\D+", ""));
        assertEquals(countBefore, countAfter);
    }

    @Test
    void unsleepNightShouldCountSessionThatStartAt7am() {
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 4, 7, 0),
                LocalDateTime.of(2025, 4, 4, 8, 0),
                SleepQuality.NORMAL
        ));
        String result = SleepAnalyzer.UNSLEEP_NIGHT.analyze(sessions);
        assertTrue(result.contains("1"));
    }

    @Test
    void chronotypeShouldReturnOWL_whenMostSessionsAreOwl() {
        List<SleepingSession> owlSessions = new ArrayList<>();
        owlSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 1, 23, 30),
                LocalDateTime.of(2025, 4, 2, 9, 30),
                SleepQuality.NORMAL
        ));
        owlSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 2, 23, 0),
                LocalDateTime.of(2025, 4, 3, 10, 0),
                SleepQuality.NORMAL
        ));
        owlSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 3, 22, 0),
                LocalDateTime.of(2025, 4, 4, 7, 0),
                SleepQuality.NORMAL
        ));

        String result = SleepAnalyzer.CHRONOTYPE.analyze(owlSessions);
        assertTrue(result.contains("Сова"));
    }

    @Test
    void chronotypeShouldReturnLARK_whenMostSessionsAreLark() {
        List<SleepingSession> larkSessions = new ArrayList<>();
        larkSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 1, 21, 0),
                LocalDateTime.of(2025, 4, 2, 6, 0),
                SleepQuality.NORMAL
        ));
        larkSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 2, 20, 30),
                LocalDateTime.of(2025, 4, 3, 6, 30),
                SleepQuality.NORMAL
        ));
        larkSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 3, 23, 0),
                LocalDateTime.of(2025, 4, 4, 8, 0),
                SleepQuality.NORMAL
        ));

        String result = SleepAnalyzer.CHRONOTYPE.analyze(larkSessions);
        assertTrue(result.contains("Жаворонок"));
    }

    @Test
    void chronotypeShouldReturnDOVE_whenMostSessionsAreDove() {
        List<SleepingSession> doveSessions = new ArrayList<>();
        doveSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 1, 22, 30),
                LocalDateTime.of(2025, 4, 2, 8, 0),
                SleepQuality.NORMAL
        ));
        doveSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 2, 22, 0),
                LocalDateTime.of(2025, 4, 3, 7, 30),
                SleepQuality.NORMAL
        ));
        doveSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 3, 23, 30),
                LocalDateTime.of(2025, 4, 4, 9, 30),
                SleepQuality.NORMAL
        ));

        String result = SleepAnalyzer.CHRONOTYPE.analyze(doveSessions);
        assertTrue(result.contains("Голубь"));
    }
}