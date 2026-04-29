package ru.practicum.common.exception.handler;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiError {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}