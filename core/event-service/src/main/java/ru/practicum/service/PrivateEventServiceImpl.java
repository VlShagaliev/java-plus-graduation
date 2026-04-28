package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.contract.CategoryExistenceProvider;
import ru.practicum.common.exception.BadRequestException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.dto.EventShortDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.repository.EventRepository;
import ru.practicum.util.EventDateTimeUtils;
import ru.practicum.util.EventDtoService;
import ru.practicum.user.contract.UserExistenceProvider;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserExistenceProvider userExistenceProvider;
    private final CategoryExistenceProvider categoryExistenceProvider;
    private final EventDtoService dtoService;

    @Override
    @Transactional
    public EventFullDto create(long userId, NewEventDto newEvent) {
        if (!userExistenceProvider.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        final long categoryId = newEvent.getCategory();
        if (!categoryExistenceProvider.existsById(categoryId)) {
            throw new NotFoundException("Category with id=" + categoryId + " was not found");
        }

        ensureEventDateNotEarlierThanTwoHoursFromNow(newEvent.getEventDate());

        final Event saved = eventRepository.save(EventMapper.from(newEvent, categoryId, userId));

        return dtoService.buildFullDto(
                saved,
                EventDateTimeUtils.defaultStart(),
                EventDateTimeUtils.defaultEnd(),
                "/events"
        );
    }

    @Override
    public List<EventShortDto> findUserEvents(long userId, int from, int size) {
        int page = from / size;

        return dtoService.buildShortDtoList(
                eventRepository.findAllByInitiatorId(userId, PageRequest.of(page, size)).stream().toList(),
                EventDateTimeUtils.defaultStart(),
                EventDateTimeUtils.defaultEnd(),
                "/events"
        );
    }

    @Override
    public EventFullDto findUserEvent(long userId, long eventId) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return dtoService.buildFullDto(
                event,
                EventDateTimeUtils.defaultStart(),
                EventDateTimeUtils.defaultEnd(),
                "/events"
        );
    }

    @Override
    @Transactional
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest updatedEvent) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Long categoryId = null;
        if (updatedEvent.getCategory() != null) {
            categoryId = updatedEvent.getCategory();
            if (!categoryExistenceProvider.existsById(categoryId)) {
                throw new NotFoundException("Category with id=" + categoryId + " was not found");
            }
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        EventMapper.updateEventProperties(updatedEvent, event, categoryId);
        ensureEventDateNotEarlierThanTwoHoursFromNow(event.getEventDate());

        return dtoService.buildFullDto(
                eventRepository.save(event),
                EventDateTimeUtils.defaultStart(),
                EventDateTimeUtils.defaultEnd(),
                "/events"
        );
    }

    private static void ensureEventDateNotEarlierThanTwoHoursFromNow(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("The event start date must not be earlier than two hours from now");
        }
    }
}