package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.contract.CompilationEventProvider;
import ru.practicum.ewm.event.api.dto.EventShortInfo;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.util.EventDateTimeUtils;
import ru.practicum.ewm.event.util.EventDtoService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class CompilationEventProviderImpl implements CompilationEventProvider {
    private final EventRepository eventRepository;
    private final EventDtoService dtoService;

    @Override
    public List<EventShortInfo> getShortEventsByIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        final Map<Long, Event> eventsById = eventRepository.findAllById(eventIds).stream()
                .collect(Collectors.toMap(
                        Event::getId,
                        Function.identity()
                ));

        final List<Event> orderedEvents = eventIds.stream()
                .map(eventsById::get)
                .filter(Objects::nonNull)
                .toList();

        return dtoService.buildShortDtoList(
                        orderedEvents,
                        EventDateTimeUtils.defaultStart(),
                        EventDateTimeUtils.defaultEnd(),
                        "/events"
                ).stream()
                .map(this::toShortInfo)
                .toList();
    }

    private EventShortInfo toShortInfo(EventShortDto dto) {
        return EventShortInfo.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .eventDate(dto.getEventDate())
                .paid(dto.isPaid())
                .views(dto.getViews())
                .confirmedRequests(dto.getConfirmedRequests())
                .category(dto.getCategory())
                .initiator(dto.getInitiator())
                .build();
    }
}