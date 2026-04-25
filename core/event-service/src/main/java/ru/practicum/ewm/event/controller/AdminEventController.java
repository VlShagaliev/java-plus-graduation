package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.event.api.dto.EventFullDto;
import ru.practicum.ewm.event.api.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.AdminEventService;
import ru.practicum.ewm.event.util.EventDateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.common.Constants.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final AdminEventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> findAllEventsByCriteria(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<EventState> states,
            @RequestParam(required = false) Set<Long> categories,
            @DateTimeFormat(pattern = DATE_TIME_FORMAT) @RequestParam(required = false) LocalDateTime rangeStart,
            @DateTimeFormat(pattern = DATE_TIME_FORMAT) @RequestParam(required = false) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
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

        return eventService.findAllByCriteria(users, states, categories, rangeStart, rangeEnd, from, size, request);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @PathVariable long id,
            @Valid @RequestBody UpdateEventAdminRequest updatedEvent,
            HttpServletRequest request) {
        return eventService.update(id, updatedEvent, request);
    }
}