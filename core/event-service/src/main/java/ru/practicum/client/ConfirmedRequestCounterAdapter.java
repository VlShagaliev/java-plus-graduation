package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.contract.ConfirmedRequestCounter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfirmedRequestCounterAdapter implements ConfirmedRequestCounter {

    private final RequestInternalClient requestInternalClient;

    @Override
    public long countConfirmedRequests(long eventId) {
        try {
            return requestInternalClient.countConfirmedRequests(eventId);
        } catch (RuntimeException ex) {
            log.warn("Failed to get confirmed requests for eventId={}, returning 0", eventId, ex);
            return 0L;
        }
    }

    @Override
    public Map<Long, Long> countConfirmedRequestsByEventIds(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        try {
            Map<Long, Long> result = requestInternalClient.countConfirmedRequestsByEventIds(eventIds);
            return result == null ? buildZeroCounts(eventIds) : fillMissingCounts(eventIds, result);
        } catch (RuntimeException ex) {
            log.warn("Failed to get confirmed requests batch for eventIds={}, returning zeros", eventIds, ex);
            return buildZeroCounts(eventIds);
        }
    }

    private static Map<Long, Long> buildZeroCounts(Collection<Long> eventIds) {
        Map<Long, Long> result = new LinkedHashMap<>();
        for (Long eventId : eventIds) {
            result.put(eventId, 0L);
        }
        return result;
    }

    private static Map<Long, Long> fillMissingCounts(
            Collection<Long> eventIds,
            Map<Long, Long> source
    ) {
        Map<Long, Long> result = new LinkedHashMap<>();
        for (Long eventId : eventIds) {
            result.put(eventId, source.getOrDefault(eventId, 0L));
        }
        return result;
    }
}