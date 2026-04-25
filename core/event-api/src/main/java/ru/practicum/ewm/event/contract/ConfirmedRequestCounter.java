package ru.practicum.ewm.event.contract;

import java.util.Collection;
import java.util.Map;

public interface ConfirmedRequestCounter {
    long countConfirmedRequests(long eventId);

    Map<Long, Long> countConfirmedRequestsByEventIds(Collection<Long> eventIds);
}