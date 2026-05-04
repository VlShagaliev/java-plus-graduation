package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Map;

@FeignClient(name = "request-service", path = "/internal/requests")
public interface RequestInternalClient {

    @GetMapping("/events/{eventId}/confirmed-count")
    long countConfirmedRequests(@PathVariable("eventId") long eventId);

    @PostMapping("/events/confirmed-count/batch")
    Map<Long, Long> countConfirmedRequestsByEventIds(@RequestBody Collection<Long> eventIds);

    @GetMapping("/events/{eventId}/users/{userId}/confirmed")
    boolean hasConfirmedParticipation(
            @PathVariable("eventId") long eventId,
            @PathVariable("userId") long userId
    );
}