package ru.practicum.ewm.event.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventDtoService {

    private final EventStatsService statsService;

    public List<EventShortDto> buildShortDtoList(
            List<Event> events,
            LocalDateTime start,
            LocalDateTime end,
            String baseUri
    ) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }

        final Map<Long, Long> views = statsService.countEventViews(events, start, end, baseUri);

        List<EventShortDto> result = new ArrayList<>(events.size());
        for (Event event : events) {
            long eventId = event.getId();
            long eventViews = views.getOrDefault(eventId, 0L);
            long confirmedRequests = statsService.countConfirmedRequests(eventId);

            EventShortDto dto = EventMapper.toShortDto(event);
            dto.setViews(eventViews);
            dto.setConfirmedRequests(confirmedRequests);

            result.add(dto);
        }

        return result;
    }

    public List<EventFullDto> buildFullDtoList(
            List<Event> events,
            LocalDateTime start,
            LocalDateTime end,
            String baseUri
    ) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }

        final Map<Long, Long> views = statsService.countEventViews(events, start, end, baseUri);

        List<EventFullDto> result = new ArrayList<>(events.size());
        for (Event event : events) {
            long eventId = event.getId();
            long eventViews = views.getOrDefault(eventId, 0L);
            long confirmedRequests = statsService.countConfirmedRequests(eventId);

            EventFullDto dto = EventMapper.toFullDto(event);
            dto.setViews(eventViews);
            dto.setConfirmedRequests(confirmedRequests);

            result.add(dto);
        }

        return result;
    }

    public EventFullDto buildFullDto(
            Event event,
            LocalDateTime start,
            LocalDateTime end,
            String baseUri
    ) {
        if (event == null) {
            return null;
        }

        final long eventId = event.getId();
        final long views = statsService
                .countEventViews(List.of(event), start, end, baseUri)
                .getOrDefault(eventId, 0L);
        final long confirmedRequests = statsService.countConfirmedRequests(eventId);

        EventFullDto dto = EventMapper.toFullDto(event);
        dto.setViews(views);
        dto.setConfirmedRequests(confirmedRequests);

        return dto;
    }
}
