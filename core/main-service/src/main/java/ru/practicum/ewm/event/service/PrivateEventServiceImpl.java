package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.util.EventDtoService;
import ru.practicum.ewm.event.util.EventDateTimeUtils;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventDtoService dtoService;

    @Override
    @Transactional
    public EventFullDto create(long userId, NewEventDto newEvent) {
        final User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        final long categoryId = newEvent.getCategory();
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
        ensureEventDateNotEarlierThanTwoHoursFromNow(newEvent.getEventDate());
        final Event saved = eventRepository.save(EventMapper.from(newEvent, category, initiator));
        return EventMapper.toFullDto(saved);
    }

    @Override
    public List<EventShortDto> findUserEvents(long userId, int from, int size) {
        return dtoService.buildShortDtoList(
                eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size)).stream().toList(),
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
        final Long categoryId = updatedEvent.getCategory();
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
        }
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        EventMapper.updateEventProperties(updatedEvent, event, category);
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