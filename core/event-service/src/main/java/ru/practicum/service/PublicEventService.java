package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.api.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.EventSortOption;

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