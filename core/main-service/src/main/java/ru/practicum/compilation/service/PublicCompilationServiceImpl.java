package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto find(long id) {
        final Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
        return CompilationMapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> findEventCompilations(Boolean pinned, int from, int size) {
        return compilationRepository.findCompilationsByPinned(pinned, PageRequest.of(from, size))
                .stream()
                .map(CompilationMapper::toDto)
                .toList();
    }
}