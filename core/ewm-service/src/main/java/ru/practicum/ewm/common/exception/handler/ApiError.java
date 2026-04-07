package ru.practicum.ewm.common.exception.handler;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private String timestamp;
}