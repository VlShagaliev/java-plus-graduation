package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventSortOption;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> findEventsByCriteria(
            String text,
            Iterable<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSortOption sort,
            int from,
            int size,
            HttpServletRequest request
    );

    EventFullDto findPublishedEvent(long id, HttpServletRequest request);
}