package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.compilation.contract.CompilationEventProvider;
import ru.practicum.event.api.dto.EventShortInfo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationEventProvider compilationEventProvider;

    @Override
    public CompilationDto find(long id) {
        final Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
        return CompilationMapper.toDto(
                compilation,
                compilationEventProvider.getShortEventsByIds(compilation.getEventIds())
        );
    }

    @Override
    public List<CompilationDto> findEventCompilations(Boolean pinned, int from, int size) {
        int page = from / size;

        Page<Compilation> compilationsPage = pinned == null
                ? compilationRepository.findAll(PageRequest.of(page, size))
                : compilationRepository.findByPinned(pinned, PageRequest.of(page, size));

        final List<Compilation> compilations = compilationsPage.getContent();

        final List<Long> allEventIds = compilations.stream()
                .map(Compilation::getEventIds)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        final Map<Long, EventShortInfo> eventsById = compilationEventProvider.getShortEventsByIds(allEventIds).stream()
                .collect(Collectors.toMap(
                        EventShortInfo::getId,
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        return compilations.stream()
                .map(compilation -> CompilationMapper.toDto(
                        compilation,
                        mapEventsInOrder(compilation.getEventIds(), eventsById)
                ))
                .toList();
    }

    private static List<EventShortInfo> mapEventsInOrder(
            List<Long> eventIds,
            Map<Long, EventShortInfo> eventsById
    ) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        return eventIds.stream()
                .map(eventsById::get)
                .filter(Objects::nonNull)
                .toList();
    }
}