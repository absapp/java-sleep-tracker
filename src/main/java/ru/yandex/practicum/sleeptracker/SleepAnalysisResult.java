package ru.yandex.practicum.sleeptracker;

import java.time.Duration;

public class SleepAnalysisResult<T> {

    private final String description;
    private final T result;

    public SleepAnalysisResult(String description, T result) {
        this.description = description;
        this.result = result;
    }

    public String getDescription() {
        return description;
    }
    
    public T getResult() {
        return result;
    }

    @Override
    public String toString() {
        if (result == null) {
            return description + ": Нет данных";
        }

        if (result instanceof Duration) {
            Duration d = (Duration) result;
            long hours = d.toHours();
            long minutes = d.toMinutesPart();
            return String.format("%s: %dч %dмин", description, hours, minutes);
        }

        if (result instanceof Chronotype) {
            Chronotype c = (Chronotype) result;
            return description + ": " + c.getDescription();
        }

        return description + ": " + result;
    }
}
