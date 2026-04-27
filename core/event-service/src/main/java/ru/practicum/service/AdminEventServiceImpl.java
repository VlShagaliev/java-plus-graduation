package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.contract.CategoryExistenceProvider;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.api.dto.EventFullDto;
import ru.practicum.api.dto.UpdateEventAdminRequest;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.repository.EventRepository;
import ru.practicum.util.EventDateTimeUtils;
import ru.practicum.util.EventDtoService;
import ru.practicum.util.UrlUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryExistenceProvider categoryExistenceProvider;
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

        int page = from / size;

        List<Event> events = eventRepository.findAllByCriteria(
                users, states, categories, rangeStart, rangeEnd, PageRequest.of(page, size)
        ).stream().toList();

        return dtoService.buildFullDtoList(events, rangeStart, rangeEnd, request.getRequestURI());
    }

    @Override
    @Transactional
    public EventFullDto update(long id, UpdateEventAdminRequest updatedEvent, HttpServletRequest request) {
        final Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        Long categoryId = null;
        if (updatedEvent.getCategory() != null) {
            categoryId = updatedEvent.getCategory();
            if (!categoryExistenceProvider.existsById(categoryId)) {
                throw new NotFoundException("Category with id=" + categoryId + " was not found");
            }
        }

        if (event.getState() == EventState.CANCELED) {
            throw new ConflictException("The event has already been canceled");
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("The event has already been published");
        }

        EventMapper.updateEventProperties(updatedEvent, event, categoryId);
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