package ru.practicum.ewm.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.request.contract.EventRequestInfo;

@FeignClient(name = "event-service", path = "/internal/events")
public interface EventInternalClient {

    @GetMapping("/{eventId}/request-info")
    EventRequestInfo getEventRequestInfo(@PathVariable("eventId") long eventId);
}