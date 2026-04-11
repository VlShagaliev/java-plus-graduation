package ru.practicum.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ViewStats;
import ru.practicum.statistics.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.common.Constants.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> findStats(
            @DateTimeFormat(pattern = DATE_TIME_FORMAT) @RequestParam LocalDateTime start,
            @DateTimeFormat(pattern = DATE_TIME_FORMAT) @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return statsService.findStats(start, end, uris, unique);
    }
}