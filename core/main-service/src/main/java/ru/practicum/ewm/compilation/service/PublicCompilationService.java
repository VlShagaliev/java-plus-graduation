package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    CompilationDto find(long id);

    List<CompilationDto> findEventCompilations(
            Boolean pinned,
            int from,
            int size
    );
}