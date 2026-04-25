package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.api.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.api.dto.NewEventDto;
import ru.practicum.ewm.event.api.dto.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventService {
    EventFullDto create(long userId, NewEventDto newEvent);

    List<EventShortDto> findUserEvents(long userId, int from, int size);

    EventFullDto findUserEvent(long userId, long eventId);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest updatedEvent);
}