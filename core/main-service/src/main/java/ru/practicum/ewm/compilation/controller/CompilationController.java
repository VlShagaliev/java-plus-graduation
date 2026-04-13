package ru.practicum.ewm.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.PublicCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationController {
    private final PublicCompilationService compilationService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto find(@PathVariable long id) {
        return compilationService.find(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> findEventCompilations(
            @RequestParam(required = false) Boolean pinned,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return compilationService.findEventCompilations(pinned, from, size);
    }
}