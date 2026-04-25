package ru.practicum.ewm.compilation.contract;

import ru.practicum.ewm.event.api.dto.EventShortInfo;

import java.util.List;

public interface CompilationEventProvider {
    List<EventShortInfo> getShortEventsByIds(List<Long> eventIds);
}