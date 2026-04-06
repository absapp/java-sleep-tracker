package ru.yandex.practicum.sleeptracker;

import java.util.List;

@FunctionalInterface
public interface SleepFunction {

    SleepAnalysisResult<?> analyze(List<SleepingSession> sleepingSession);
}
