package ru.practicum.stats.service.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.common.exception.BadRequestException;
import ru.practicum.stats.service.statistics.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public List<ViewStats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("The start must be earlier than or equal to the end");
        }
        return unique ?
                statsRepository.findStatsUnique(start, end, uris) :
                statsRepository.findStatsAll(start, end, uris);
    }
}