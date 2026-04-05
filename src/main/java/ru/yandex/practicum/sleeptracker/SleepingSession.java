
package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

public class SleepingSession {
    private final LocalDateTime startSleeping;
    private final LocalDateTime endSleeping;
    private final SleepQuality sleepQuality;
    private final static LocalTime START_NIGHT = LocalTime.of(00, 00);
    private final static LocalTime END_NIGHT = LocalTime.of(06, 00);

    public SleepingSession(LocalDateTime startSleeping, LocalDateTime endSleeping, SleepQuality sleepQuality) {
        if (startSleeping.isAfter(endSleeping)) {
            throw new IllegalArgumentException("Время начала не может быть позже времени окончания");
        }

        this.startSleeping = startSleeping;
        this.endSleeping = endSleeping;
        this.sleepQuality = sleepQuality;
    }

    public boolean isUnSleepDay() {
        Period period = Period.between(startSleeping.toLocalDate(), endSleeping.toLocalDate());
        return period.isZero() && startSleeping.toLocalTime().isAfter(END_NIGHT);
    }

    public boolean isNightSleep() {
        if (isUnSleepDay()) return false;

        if (startSleeping.toLocalDate().isBefore(endSleeping.toLocalDate())) {
            return true;
        }

        LocalTime start = startSleeping.toLocalTime();
        LocalTime end = endSleeping.toLocalTime();

        return isIntersectsNightInterval(start, end);
    }

    private boolean isIntersectsNightInterval(LocalTime start, LocalTime end) {
        return start.isBefore(END_NIGHT) && end.isAfter(START_NIGHT);
    }

    public Chronotype getChronotype() {
        LocalTime start = startSleeping.toLocalTime();
        LocalTime end = endSleeping.toLocalTime();

        if (start.getHour() >= 23 && end.getHour() >= 9) {
            return Chronotype.OWL;
        }

        if (start.getHour() < 22 && end.getHour() < 7) {
            return Chronotype.LARK;
        }

        return Chronotype.DOVE;
    }

    public Duration getDuration() {
        return Duration.between(startSleeping, endSleeping);
    }

    public LocalDateTime getStartSleeping() {
        return startSleeping;
    }

    public LocalDateTime getEndSleeping() {
        return endSleeping;
    }

    public SleepQuality getSleepQuality() {
        return sleepQuality;
    }

}
