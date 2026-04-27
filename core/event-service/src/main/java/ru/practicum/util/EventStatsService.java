package ru.practicum.ewm.event.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.contract.ConfirmedRequestCounter;
import ru.practicum.ewm.event.mapper.HitMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStatsService {
    private final StatsClient statsClient;
    private final ConfirmedRequestCounter confirmedRequestCounter;

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

        final List<ViewStats> stats = findStatsSafely(start, end, uris, baseUri);
        if (stats.isEmpty()) {
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
        return confirmedRequestCounter.countConfirmedRequests(eventId);
    }

    public Map<Long, Long> countConfirmedRequests(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return confirmedRequestCounter.countConfirmedRequestsByEventIds(eventIds);
    }

    public void sendHits(List<Event> events, HttpServletRequest request) {
        if (events == null || events.isEmpty()) {
            return;
        }

        final HitCreateDto hit = HitMapper.buildCreateHit(request);

        for (Event e : events) {
            hit.setUri(request.getRequestURI() + "/" + e.getId());
            hit.setTimestamp(LocalDateTime.now());
            sendHitSafely(hit, "Failed to send hit for eventId=" + e.getId() + ", uri='" + hit.getUri() + "'");
        }
    }

    public void sendHit(HttpServletRequest request) {
        final HitCreateDto hit = HitMapper.buildCreateHit(request);
        sendHitSafely(hit, "Failed to send single hit for uri='" + hit.getUri() + "'");
    }

    private List<ViewStats> findStatsSafely(
            LocalDateTime start,
            LocalDateTime end,
            Set<String> uris,
            String baseUri) {
        try {
            return statsClient.findStats(start, end, uris, true);
        } catch (RuntimeException ex) {
            log.warn("Failed to get stats from stats-service, returning zero views. URIs={}, baseUri='{}'",
                    uris, baseUri, ex);
            return Collections.emptyList();
        }
    }

    private void sendHitSafely(HitCreateDto hit, String message) {
        try {
            statsClient.hit(hit);
        } catch (RuntimeException ex) {
            log.warn(message, ex);
        }
    }
}