package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.contract.EventRequestInfo;
import ru.practicum.ewm.request.contract.EventRequestInfoProvider;

@Component
@RequiredArgsConstructor
public class EventRequestInfoProviderImpl implements EventRequestInfoProvider {
    private final EventRepository eventRepository;

    @Override
    public EventRequestInfo getEventRequestInfo(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        return EventRequestInfo.builder()
                .eventId(event.getId())
                .initiatorId(event.getInitiatorId())
                .published(event.getState() == ru.practicum.ewm.event.model.EventState.PUBLISHED)
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .build();
    }
}