package ru.practicum.ewm.catalog.compilation.service;

import ru.practicum.ewm.catalog.compilation.dto.CompilationDto;
import ru.practicum.ewm.catalog.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.catalog.compilation.dto.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto create(NewCompilationDto newCompilation);

    CompilationDto update(long id, UpdateCompilationRequest request);

    void delete(long id);
}