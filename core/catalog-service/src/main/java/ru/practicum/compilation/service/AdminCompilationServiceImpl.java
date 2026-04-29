package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.compilation.contract.CompilationEventProvider;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationEventProvider compilationEventProvider;

    @Override
    public CompilationDto create(NewCompilationDto newCompilation) {
        final Compilation saved = compilationRepository.save(CompilationMapper.from(newCompilation));
        return CompilationMapper.toDto(
                saved,
                compilationEventProvider.getShortEventsByIds(saved.getEventIds())
        );
    }

    @Override
    public CompilationDto update(long id, UpdateCompilationRequest request) {
        final Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));

        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getEvents() != null) {
            compilation.setEventIds(new ArrayList<>(request.getEvents()));
        }

        final Compilation saved = compilationRepository.save(compilation);
        return CompilationMapper.toDto(
                saved,
                compilationEventProvider.getShortEventsByIds(saved.getEventIds())
        );
    }

    @Override
    public void delete(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Compilation with id=" + id + " was not found");
        }
        compilationRepository.deleteById(id);
    }
}