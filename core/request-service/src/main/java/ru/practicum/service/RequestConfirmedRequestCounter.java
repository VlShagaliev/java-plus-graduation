package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.contract.ConfirmedRequestCounter;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.ConfirmedRequestCountProjection;
import ru.practicum.repository.RequestRepository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestConfirmedRequestCounter implements ConfirmedRequestCounter {
    private final RequestRepository requestRepository;

    @Override
    public long countConfirmedRequests(long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    @Override
    public Map<Long, Long> countConfirmedRequestsByEventIds(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        return requestRepository.countConfirmedRequestsByEventIds(eventIds, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.toMap(
                        ConfirmedRequestCountProjection::getEventId,
                        ConfirmedRequestCountProjection::getConfirmedCount,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }
}