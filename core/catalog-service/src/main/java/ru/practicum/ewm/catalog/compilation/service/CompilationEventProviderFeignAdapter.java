package ru.practicum.ewm.catalog.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.contract.CompilationEventProvider;
import ru.practicum.ewm.event.api.dto.EventShortInfo;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompilationEventProviderFeignAdapter implements CompilationEventProvider {

    private final CompilationEventInternalClient compilationEventInternalClient;

    @Override
    public List<EventShortInfo> getShortEventsByIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        try {
            return compilationEventInternalClient.getShortEventsByIds(eventIds);
        } catch (RuntimeException ex) {
            log.warn("Failed to get events for compilation, returning empty list. eventIds={}", eventIds, ex);
            return List.of();
        }
    }
}