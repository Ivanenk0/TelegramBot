package com.mateacademy.telegrambot;

public enum ProficiencyLevel {
    JUNIOR,
    MIDDLE,
    SENIOR;

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
