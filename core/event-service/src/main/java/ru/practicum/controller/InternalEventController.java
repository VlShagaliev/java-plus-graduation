package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.contract.EventRequestInfo;
import ru.practicum.request.contract.EventRequestInfoProvider;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
public class InternalEventController {

    private final EventRequestInfoProvider eventRequestInfoProvider;

    @GetMapping("/{eventId}/request-info")
    public EventRequestInfo getEventRequestInfo(@PathVariable long eventId) {
        return eventRequestInfoProvider.getEventRequestInfo(eventId);
    }
}