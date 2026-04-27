package ru.practicum.stats.service.hit.service;

import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;

public interface HitService {
    HitDto create(HitCreateDto hitDto);
}
