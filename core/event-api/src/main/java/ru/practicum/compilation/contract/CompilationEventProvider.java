package ru.practicum.compilation.contract;

import ru.practicum.dto.EventShortInfo;

import java.util.List;

public interface CompilationEventProvider {
    List<EventShortInfo> getShortEventsByIds(List<Long> eventIds);
}