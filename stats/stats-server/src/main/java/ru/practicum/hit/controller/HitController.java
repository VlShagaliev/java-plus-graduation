package ru.practicum.stats.service.hit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.service.hit.service.HitService;

@RestController
@RequestMapping("/hit")
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto create(@Valid @RequestBody HitCreateDto hitDto) {
        return hitService.create(hitDto);
    }
}
