package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto create(NewCompilationDto newCompilation);

    CompilationDto update(long id, UpdateCompilationRequest request);

    void delete(long id);
}