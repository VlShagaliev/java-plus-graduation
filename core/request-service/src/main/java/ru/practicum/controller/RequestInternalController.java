package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.RequestConfirmedRequestCounter;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class RequestInternalController {

    private final RequestConfirmedRequestCounter confirmedRequestCounter;

    @GetMapping("/events/{eventId}/confirmed-count")
    public long countConfirmedRequests(@PathVariable long eventId) {
        return confirmedRequestCounter.countConfirmedRequests(eventId);
    }

    @PostMapping("/events/confirmed-count/batch")
    public Map<Long, Long> countConfirmedRequestsByEventIds(@RequestBody Collection<Long> eventIds) {
        return confirmedRequestCounter.countConfirmedRequestsByEventIds(eventIds);
    }

    @GetMapping("/events/{eventId}/users/{userId}/confirmed")
    public boolean hasConfirmedParticipation(@PathVariable long eventId, @PathVariable long userId) {
        return confirmedRequestCounter.hasConfirmedParticipation(eventId, userId);
    }
}