package ru.practicum.stats.service.hit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.service.hit.mapper.HitMapper;
import ru.practicum.stats.service.hit.model.Hit;
import ru.practicum.stats.service.hit.repository.HitRepository;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    @Transactional
    public HitDto create(HitCreateDto hitDto) {
        final Hit saved = hitRepository.save(HitMapper.mapToHit(hitDto));
        return HitMapper.mapToDto(saved);
    }
}
