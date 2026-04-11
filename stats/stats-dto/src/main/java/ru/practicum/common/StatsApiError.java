package ru.practicum.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatsApiError {
    private String status;
    private String reason;
    private String message;
    private String timestamp;
}