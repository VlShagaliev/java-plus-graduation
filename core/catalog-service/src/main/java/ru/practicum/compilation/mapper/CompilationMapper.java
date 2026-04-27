package ru.practicum.ewm.catalog.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.catalog.compilation.dto.CompilationDto;
import ru.practicum.ewm.catalog.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.catalog.compilation.model.Compilation;
import ru.practicum.ewm.event.api.dto.EventShortInfo;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {
    public static Compilation from(NewCompilationDto newCompilation) {
        return Compilation.builder()
                .title(newCompilation.getTitle())
                .pinned(newCompilation.getPinned() != null ? newCompilation.getPinned() : false)
                .eventIds(newCompilation.getEvents() != null ? List.copyOf(newCompilation.getEvents()) : List.of())
                .build();
    }

    public static CompilationDto toDto(Compilation compilation, List<EventShortInfo> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(events == null ? List.of() : events)
                .build();
    }
}