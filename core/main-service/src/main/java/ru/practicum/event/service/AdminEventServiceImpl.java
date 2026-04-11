package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.common.exception.BadRequestException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.util.EventDateTimeUtils;
import ru.practicum.event.util.EventDtoService;
import ru.practicum.event.util.UrlUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventDtoService dtoService;

    @Override
    public List<EventFullDto> findAllByCriteria(
            Iterable<Long> users,
            Iterable<EventState> states,
            Iterable<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size,
            HttpServletRequest request) {

        if (rangeStart == null) {
            rangeStart = EventDateTimeUtils.defaultStart();
        }
        if (rangeEnd == null) {
            rangeEnd = EventDateTimeUtils.defaultEnd();
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("The rangeStart must be earlier than or equal to the rangeEnd");
        }

        List<Event> events = eventRepository.findAllByCriteria(
                users, states, categories, rangeStart, rangeEnd, PageRequest.of(from, size)
        ).stream().toList();

        return dtoService.buildFullDtoList(events, rangeStart, rangeEnd, request.getRequestURI());
    }

    @Override
    @Transactional
    public EventFullDto update(long id, UpdateEventAdminRequest updatedEvent, HttpServletRequest request) {
        final Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        final Long categoryId = updatedEvent.getCategory();
        Category category = null;

        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
        }

        if (event.getState() == EventState.CANCELED) {
            throw new ConflictException("The event has already been canceled");
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("The event has already been published");
        }

        EventMapper.updateEventProperties(updatedEvent, event, category);
        ensureStartDateIsAtLeastAnHourAfterPublication(event.getEventDate(), event.getPublishedOn());

        return dtoService.buildFullDto(
                eventRepository.save(event),
                EventDateTimeUtils.defaultStart(),
                EventDateTimeUtils.defaultEnd(),
                UrlUtils.removeTrailingNumberSegment(request.getRequestURI())
        );
    }

    public static void ensureStartDateIsAtLeastAnHourAfterPublication(
            LocalDateTime startDate,
            LocalDateTime publicationDate) {

        if (publicationDate != null && startDate.isBefore(publicationDate.plusHours(1))) {
            throw new ConflictException("Start date must be at least one hour after publication date");
        }
    }
}
