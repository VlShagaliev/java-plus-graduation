package ru.practicum.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {
    public static Compilation from(NewCompilationDto newCompilation, List<Event> events) {
        return Compilation.builder()
                .title(newCompilation.getTitle())
                .pinned(
                        newCompilation.getPinned() != null ?
                                newCompilation.getPinned() :
                                false
                )
                .events(events)
                .build();
    }

    public static CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(
                        compilation.getEvents() != null ?
                                compilation.getEvents().stream().map(EventMapper::toShortDto).toList() :
                                List.of()
                )
                .build();
    }
}