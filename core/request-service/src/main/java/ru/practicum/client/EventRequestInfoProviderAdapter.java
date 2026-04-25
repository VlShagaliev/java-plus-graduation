package ru.practicum.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.request.contract.EventRequestInfo;
import ru.practicum.request.contract.EventRequestInfoProvider;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventRequestInfoProviderAdapter implements EventRequestInfoProvider {

    private final EventInternalClient eventInternalClient;

    @Override
    public EventRequestInfo getEventRequestInfo(long eventId) {
        try {
            return eventInternalClient.getEventRequestInfo(eventId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        } catch (RuntimeException ex) {
            log.warn("Failed to get event request info for eventId={}, returning 404 instead of 5xx", eventId, ex);
            throw new NotFoundException("Event with id=" + eventId + " was not found or is temporarily unavailable");
        }
    }
}