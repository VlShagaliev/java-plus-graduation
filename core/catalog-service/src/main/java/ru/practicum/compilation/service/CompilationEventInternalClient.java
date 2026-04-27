package ru.practicum.ewm.catalog.compilation.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.event.api.dto.EventShortInfo;

import java.util.List;

@FeignClient(
        contextId = "compilationEventInternalClient",
        name = "event-service",
        path = "/internal/events"
)
public interface CompilationEventInternalClient {

    @PostMapping("/short")
    List<EventShortInfo> getShortEventsByIds(@RequestBody List<Long> eventIds);
}