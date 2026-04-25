package ru.practicum.ewm.request.contract;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventRequestInfo {
    long eventId;
    long initiatorId;
    boolean published;
    long participantLimit;
    boolean requestModeration;
}