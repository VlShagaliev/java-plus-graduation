package ru.practicum.stats.client.exception;

import lombok.Getter;
import ru.practicum.stats.common.StatsApiError;

@Getter
public class StatsClientException extends RuntimeException {
    private final StatsApiError response;

    public StatsClientException(String message, StatsApiError response) {
        super(message);
        this.response = response;
    }
}