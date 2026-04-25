package ru.practicum.ewm.catalog.compilation.service;

import ru.practicum.ewm.catalog.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    CompilationDto find(long id);

    List<CompilationDto> findEventCompilations(
            Boolean pinned,
            int from,
            int size
    );
}