package ru.practicum.stats.service.statistics.service;

import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStats> findStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique
    );
}