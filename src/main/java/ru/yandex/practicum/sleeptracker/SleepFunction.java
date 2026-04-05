package ru.yandex.practicum.sleeptracker;

import java.util.List;

@FunctionalInterface
public interface SleepFunction {

    String analyze(List<SleepingSession> sleepingSession);
}
