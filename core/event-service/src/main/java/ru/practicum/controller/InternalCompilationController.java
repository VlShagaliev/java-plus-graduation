package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.contract.CompilationEventProvider;
import ru.practicum.ewm.event.api.dto.EventShortInfo;

import java.util.List;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
public class InternalCompilationController {

    private final CompilationEventProvider compilationEventProvider;

    @PostMapping("/short")
    public List<EventShortInfo> getShortEventsByIds(@RequestBody List<Long> eventIds) {
        return compilationEventProvider.getShortEventsByIds(eventIds);
    }
}