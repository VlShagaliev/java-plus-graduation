package ru.practicum.exception;

import lombok.Getter;
import ru.practicum.common.StatsApiError;

@Getter
public class StatsClientException extends RuntimeException {
    private final StatsApiError response;

    public StatsClientException(String message, StatsApiError response) {
        super(message);
        this.response = response;
    }
}