package ru.practicum.event.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.HitMapper;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.StatsClient;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStatsService {
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    public Map<Long, Long> countEventViews(
            List<Event> events,
            LocalDateTime start,
            LocalDateTime end,
            String baseUri) {

        if (events == null || events.isEmpty()) {
            return Collections.emptyMap();
        }

        final Set<String> uris = events.stream()
                .map(e -> baseUri + "/" + e.getId())
                .collect(Collectors.toSet());

        if (uris.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ViewStats> stats;
        try {
            stats = statsClient.findStats(start, end, uris, true);
        } catch (RuntimeException ex) {
            log.warn("Failed to get stats from stats-service, returning zero views. URIs={}", uris, ex);
            return Collections.emptyMap();
        }

        final Map<Long, Long> eventViews = new HashMap<>();
        for (ViewStats s : stats) {
            final String strId = s.getUri().replace(baseUri + "/", "");
            if (!strId.equals(baseUri)) {
                try {
                    final long eventId = Long.parseLong(strId);
                    final long views = eventViews.getOrDefault(eventId, 0L);
                    eventViews.put(eventId, views + s.getHits());
                } catch (NumberFormatException ex) {
                    log.warn("Cannot parse event id from stats uri: uri='{}', baseUri='{}'", s.getUri(), baseUri, ex);
                }
            }
        }
        return eventViews;
    }

    public long countConfirmedRequests(long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    public void sendHits(List<Event> events, HttpServletRequest request) {
        if (events == null || events.isEmpty()) {
            return;
        }

        final HitCreateDto hit = HitMapper.buildCreateHit(request);

        for (Event e : events) {
            hit.setUri(request.getRequestURI() + "/" + e.getId());
            hit.setTimestamp(LocalDateTime.now());
            try {
                statsClient.hit(hit);
            } catch (RuntimeException ex) {
                log.warn("Failed to send hit for eventId={}, uri='{}'", e.getId(), hit.getUri(), ex);
            }
        }
    }

    public void sendHit(HttpServletRequest request) {
        final HitCreateDto hit = HitMapper.buildCreateHit(request);
        try {
            HitDto ignored = statsClient.hit(hit);
        } catch (RuntimeException ex) {
            log.warn("Failed to send single hit for uri='{}'", hit.getUri(), ex);
        }
    }
}
