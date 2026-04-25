package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.api.dto.EventFullDto;
import ru.practicum.ewm.event.api.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventFullDto> findAllByCriteria(
            Iterable<Long> users,
            Iterable<EventState> states,
            Iterable<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size,
            HttpServletRequest request
    );

    EventFullDto update(
            long id,
            UpdateEventAdminRequest updatedEvent,
            HttpServletRequest request
    );
}