package ru.practicum.hit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.HitDto;
import ru.practicum.hit.mapper.HitMapper;
import ru.practicum.hit.model.Hit;
import ru.practicum.hit.repository.HitRepository;

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
