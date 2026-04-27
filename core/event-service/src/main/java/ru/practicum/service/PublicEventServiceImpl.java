package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.exception.BadRequestException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.api.dto.EventFullDto;
import ru.practicum.dto.EventSortOption;
import ru.practicum.dto.EventShortDto;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.repository.EventRepository;
import ru.practicum.util.EventDateTimeUtils;
import ru.practicum.util.EventDtoService;
import ru.practicum.util.EventStatsService;
import ru.practicum.util.UrlUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final EventStatsService statsService;
    private final EventDtoService dtoService;

    @Override
    public List<EventShortDto> findEventsByCriteria(
            String text,
            Iterable<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSortOption sort,
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

        statsService.sendHit(request);

        int page = from / size;
        final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        if (text == null) {
            text = "";
        }

        final List<Event> foundEvents = eventRepository.findAllPublishedByCriteria(
                text, categories, paid, rangeStart, rangeEnd, pageable
        ).getContent();

        final List<Event> events;
        if (onlyAvailable) {
            final Map<Long, Long> confirmedRequests = statsService.countConfirmedRequests(
                    foundEvents.stream()
                            .map(Event::getId)
                            .toList()
            );

            events = new ArrayList<>();
            for (Event event : foundEvents) {
                long confirmedCount = confirmedRequests.getOrDefault(event.getId(), 0L);
                if (event.getParticipantLimit() == 0 || confirmedCount < event.getParticipantLimit()) {
                    events.add(event);
                }
            }
        } else {
            events = foundEvents;
        }

        final List<EventShortDto> result = new ArrayList<>(
                dtoService.buildShortDtoList(events, rangeStart, rangeEnd, request.getRequestURI())
        );

        if (sort == EventSortOption.VIEWS) {
            result.sort((e1, e2) -> Long.compare(e2.getViews(), e1.getViews()));
        }

        return result;
    }

    @Override
    public EventFullDto findPublishedEvent(long id, HttpServletRequest request) {
        final Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " not found or not published"));

        statsService.sendHit(request);

        return dtoService.buildFullDto(
                event,
                EventDateTimeUtils.defaultStart(),
                EventDateTimeUtils.defaultEnd(),
                UrlUtils.removeTrailingNumberSegment(request.getRequestURI())
        );
    }
}