package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        SleepAnalysisResult<?> result = SleepAnalyzer.TOTAL_SESSIONS.analyze(sessions);
        assertEquals(3L, result.getResult());
        assertTrue(result.toString().contains("3"));
    }

    @Test
    void totalSessionShouldReturnZero_whenEmptyList() {
        SleepAnalysisResult<?> result = SleepAnalyzer.TOTAL_SESSIONS.analyze(new ArrayList<>());
        assertEquals(0L, result.getResult());
        assertTrue(result.toString().contains("0"));
    }

    @Test
    void shortestSleepShouldReturnShortestDuration() {
        SleepAnalysisResult<?> result = SleepAnalyzer.SHORTEST_SLEEP.analyze(sessions);
        Duration shortest = (Duration) result.getResult();
        assertEquals(4, shortest.toHours());
        assertTrue(result.toString().contains("4ч"));
    }

    @Test
    void shortestSleepShouldReturnNoData_whenEmptyList() {
        SleepAnalysisResult<?> result = SleepAnalyzer.SHORTEST_SLEEP.analyze(new ArrayList<>());
        assertNull(result.getResult());
        assertEquals("Самая короткая сессия: Нет данных", result.toString());
    }

    @Test
    void longestSleepShouldReturnLongestDuration() {
        SleepAnalysisResult<?> result = SleepAnalyzer.LONGEST_SLEEP.analyze(sessions);
        Duration longest = (Duration) result.getResult();
        assertEquals(9, longest.toHours());
        assertTrue(result.toString().contains("9ч"));
    }

    @Test
    void longestSleepShouldReturnNoData_whenEmptyList() {
        SleepAnalysisResult<?> result = SleepAnalyzer.LONGEST_SLEEP.analyze(new ArrayList<>());
        assertNull(result.getResult());
        assertEquals("Самая долгая сессия: Нет данных", result.toString());
    }

    @Test
    void averageDurationShouldCalculateCorrectAverage() {
        SleepAnalysisResult<?> result = SleepAnalyzer.AVERAGE_DURATION.analyze(sessions);
        Double avg = (Double) result.getResult();
        assertEquals(440.0, avg, 0.01);
        assertTrue(result.toString().contains("440.0") || result.toString().contains("440"));
    }

    @Test
    void averageDurationShouldReturnZero_whenEmptyList() {
        SleepAnalysisResult<?> result = SleepAnalyzer.AVERAGE_DURATION.analyze(new ArrayList<>());
        assertEquals(0.0, (Double) result.getResult(), 0.01);
        assertTrue(result.toString().contains("0"));
    }

    @Test
    void badSessionShouldCountOnlyBadSessions() {
        SleepAnalysisResult<?> result = SleepAnalyzer.BAD_SESSIONS.analyze(sessions);
        assertEquals(1L, result.getResult());
        assertTrue(result.toString().contains("1"));
    }

    @Test
    void badSessionShouldReturnZero_whenNoBadSessions() {
        List<SleepingSession> goodSessions = new ArrayList<>();
        goodSessions.add(new SleepingSession(
                LocalDateTime.now(), LocalDateTime.now().plusHours(8), SleepQuality.GOOD
        ));
        SleepAnalysisResult<?> result = SleepAnalyzer.BAD_SESSIONS.analyze(goodSessions);
        assertEquals(0L, result.getResult());
        assertTrue(result.toString().contains("0"));
    }

    @Test
    void unsleepNightShouldCountOnlyUnsleepDays() {
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 4, 7, 0),
                LocalDateTime.of(2025, 4, 4, 9, 0),
                SleepQuality.NORMAL
        ));
        SleepAnalysisResult<?> result = SleepAnalyzer.UNSLEEP_NIGHT.analyze(sessions);
        assertEquals(1L, result.getResult());
        assertTrue(result.toString().contains("1"));
    }

    @Test
    void unsleepNightShouldReturnZero_whenNoUnsleepDays() {
        List<SleepingSession> nightSessions = new ArrayList<>();
        nightSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 1, 23, 0),
                LocalDateTime.of(2025, 4, 2, 7, 0),
                SleepQuality.GOOD
        ));
        SleepAnalysisResult<?> result = SleepAnalyzer.UNSLEEP_NIGHT.analyze(nightSessions);
        assertEquals(0L, result.getResult());
        assertTrue(result.toString().contains("0"));
    }

    @Test
    void unsleepNightShouldNotCountSessionsThatStartBefore6am() {
        List<SleepingSession> testSessions = new ArrayList<>(sessions);
        testSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 4, 5, 0),
                LocalDateTime.of(2025, 4, 4, 6, 30),
                SleepQuality.NORMAL
        ));
        SleepAnalysisResult<?> result1 = SleepAnalyzer.UNSLEEP_NIGHT.analyze(testSessions);

        testSessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 5, 5, 0),
                LocalDateTime.of(2025, 4, 5, 6, 30),
                SleepQuality.NORMAL
        ));
        SleepAnalysisResult<?> result2 = SleepAnalyzer.UNSLEEP_NIGHT.analyze(testSessions);

        assertEquals(result1.getResult(), result2.getResult());
    }

    @Test
    void unsleepNightShouldCountSessionThatStartAt7am() {
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 4, 4, 7, 0),
                LocalDateTime.of(2025, 4, 4, 8, 0),
                SleepQuality.NORMAL
        ));
        SleepAnalysisResult<?> result = SleepAnalyzer.UNSLEEP_NIGHT.analyze(sessions);
        assertEquals(1L, result.getResult());
        assertTrue(result.toString().contains("1"));
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

        SleepAnalysisResult<?> result = SleepAnalyzer.CHRONOTYPE.analyze(owlSessions);
        assertEquals(Chronotype.OWL, result.getResult());
        assertTrue(result.toString().contains("Сова"));
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

        SleepAnalysisResult<?> result = SleepAnalyzer.CHRONOTYPE.analyze(larkSessions);
        assertEquals(Chronotype.LARK, result.getResult());
        assertTrue(result.toString().contains("Жаворонок"));
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

        SleepAnalysisResult<?> result = SleepAnalyzer.CHRONOTYPE.analyze(doveSessions);
        assertEquals(Chronotype.DOVE, result.getResult());
        assertTrue(result.toString().contains("Голубь"));
    }
}