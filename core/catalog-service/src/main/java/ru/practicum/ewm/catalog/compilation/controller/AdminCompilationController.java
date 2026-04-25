package ru.practicum.ewm.catalog.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.catalog.compilation.dto.CompilationDto;
import ru.practicum.ewm.catalog.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.catalog.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.catalog.compilation.service.AdminCompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {
    private final AdminCompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilation) {
        return compilationService.create(newCompilation);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(
            @PathVariable long id,
            @Valid @RequestBody UpdateCompilationRequest request) {
        return compilationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long id) {
        compilationService.delete(id);
    }
}