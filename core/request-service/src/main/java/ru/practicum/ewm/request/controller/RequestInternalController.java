package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.service.RequestConfirmedRequestCounter;

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
}