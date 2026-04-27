package ru.practicum.exception;

public class StatsServerUnavailableException extends RuntimeException {
    public StatsServerUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}