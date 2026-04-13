package ru.practicum.hit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.HitDto;
import ru.practicum.hit.service.HitService;

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
