package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.contract.CategoryShortInfoProvider;
import ru.practicum.dto.CategoryShortInfo;
import ru.practicum.api.dto.EventFullDto;
import ru.practicum.dto.UserShortInfo;
import ru.practicum.dto.EventShortDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.user.contract.UserShortInfoProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EventDtoService {

    private final EventStatsService statsService;
    private final UserShortInfoProvider userShortInfoProvider;
    private final CategoryShortInfoProvider categoryShortInfoProvider;

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
        final Map<Long, Long> confirmedRequests = statsService.countConfirmedRequests(extractEventIds(events));
        final Map<Long, CategoryShortInfo> categories =
                categoryShortInfoProvider.getShortInfoByIds(extractCategoryIds(events));
        final Map<Long, UserShortInfo> initiators =
                userShortInfoProvider.getShortInfoByIds(extractInitiatorIds(events));

        List<EventShortDto> result = new ArrayList<>(events.size());
        for (Event event : events) {
            long eventId = event.getId();

            EventShortDto dto = EventMapper.toShortDto(event);
            dto.setCategory(categories.get(event.getCategoryId()));
            dto.setInitiator(initiators.get(event.getInitiatorId()));
            dto.setViews(views.getOrDefault(eventId, 0L));
            dto.setConfirmedRequests(confirmedRequests.getOrDefault(eventId, 0L));

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
        final Map<Long, Long> confirmedRequests = statsService.countConfirmedRequests(extractEventIds(events));
        final Map<Long, CategoryShortInfo> categories =
                categoryShortInfoProvider.getShortInfoByIds(extractCategoryIds(events));
        final Map<Long, UserShortInfo> initiators =
                userShortInfoProvider.getShortInfoByIds(extractInitiatorIds(events));

        List<EventFullDto> result = new ArrayList<>(events.size());
        for (Event event : events) {
            long eventId = event.getId();

            EventFullDto dto = EventMapper.toFullDto(event);
            dto.setCategory(categories.get(event.getCategoryId()));
            dto.setInitiator(initiators.get(event.getInitiatorId()));
            dto.setViews(views.getOrDefault(eventId, 0L));
            dto.setConfirmedRequests(confirmedRequests.getOrDefault(eventId, 0L));

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
        Objects.requireNonNull(event, "event must not be null");

        final long eventId = event.getId();
        final long views = statsService
                .countEventViews(List.of(event), start, end, baseUri)
                .getOrDefault(eventId, 0L);
        final long confirmedRequests = statsService.countConfirmedRequests(eventId);

        EventFullDto dto = EventMapper.toFullDto(event);
        dto.setCategory(categoryShortInfoProvider.getShortInfo(event.getCategoryId()));
        dto.setInitiator(userShortInfoProvider.getShortInfo(event.getInitiatorId()));
        dto.setViews(views);
        dto.setConfirmedRequests(confirmedRequests);

        return dto;
    }

    private static Set<Long> extractEventIds(List<Event> events) {
        Set<Long> ids = new LinkedHashSet<>();
        for (Event event : events) {
            ids.add(event.getId());
        }
        return ids;
    }

    private static Set<Long> extractCategoryIds(List<Event> events) {
        Set<Long> ids = new LinkedHashSet<>();
        for (Event event : events) {
            ids.add(event.getCategoryId());
        }
        return ids;
    }

    private static Set<Long> extractInitiatorIds(List<Event> events) {
        Set<Long> ids = new LinkedHashSet<>();
        for (Event event : events) {
            ids.add(event.getInitiatorId());
        }
        return ids;
    }
}