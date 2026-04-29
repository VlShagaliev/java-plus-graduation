package ru.practicum.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventService {
    EventFullDto create(long userId, NewEventDto newEvent);

    List<EventShortDto> findUserEvents(long userId, int from, int size);

    EventFullDto findUserEvent(long userId, long eventId);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest updatedEvent);
}