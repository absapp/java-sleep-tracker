package ru.yandex.practicum.sleeptracker;

public enum Chronotype {
    OWL("Сова"),
    LARK("Жаворонок"),
    DOVE("Голубь");

    private final String description;

    Chronotype(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}