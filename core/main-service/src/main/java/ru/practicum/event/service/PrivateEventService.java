package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventService {
    EventFullDto create(long userId, NewEventDto newEvent);

    List<EventShortDto> findUserEvents(long userId, int from, int size);

    EventFullDto findUserEvent(long userId, long eventId);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest updatedEvent);
}