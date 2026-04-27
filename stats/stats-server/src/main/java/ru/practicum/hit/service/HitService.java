package ru.practicum.hit.service;

import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.HitDto;

public interface HitService {
    HitDto create(HitCreateDto hitDto);
}
