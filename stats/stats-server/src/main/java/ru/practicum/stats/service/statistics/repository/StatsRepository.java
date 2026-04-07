package ru.practicum.stats.service.statistics.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.hit.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends Repository<Hit, Long> {
    @Query("SELECT new ru.practicum.stats.dto.ViewStats(h.app, h.uri, COUNT(h)) " +
            "FROM Hit h " +
            "WHERE (h.timestamp BETWEEN ?1 AND ?2) " +
            "AND ((?3) IS NULL OR h.uri IN ?3) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h) DESC")
    List<ViewStats> findStatsAll(LocalDateTime start, LocalDateTime end, Iterable<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE (h.timestamp BETWEEN ?1 AND ?2) " +
            "AND ((?3) IS NULL OR h.uri IN ?3) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findStatsUnique(LocalDateTime start, LocalDateTime end, Iterable<String> uris);
}