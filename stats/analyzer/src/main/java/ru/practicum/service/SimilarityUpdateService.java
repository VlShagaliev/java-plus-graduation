package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EventSimilarityEntity;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.avro.EventSimilarityAvro;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarityUpdateService {

    private final EventSimilarityRepository repository;

    @Transactional
    public void process(EventSimilarityAvro similarity) {
        if (similarity == null) {
            return;
        }

        long first = Math.min(similarity.getEventA(), similarity.getEventB());
        long second = Math.max(similarity.getEventA(), similarity.getEventB());
        Instant timestamp = similarity.getTimestamp() == null ? Instant.now() : similarity.getTimestamp();

        repository.findByEventAAndEventB(first, second)
                .ifPresentOrElse(
                        existing -> updateExisting(existing, similarity.getScore(), timestamp),
                        () -> createNew(first, second, similarity.getScore(), timestamp)
                );
    }

    private void updateExisting(EventSimilarityEntity existing, double score, Instant timestamp) {
        existing.setScore(score);
        existing.setUpdatedAt(timestamp);
        repository.save(existing);

        log.info(
                "Обновлено сходство событий: eventA={}, eventB={}, score={}",
                existing.getEventA(),
                existing.getEventB(),
                existing.getScore()
        );
    }

    private void createNew(long eventA, long eventB, double score, Instant timestamp) {
        EventSimilarityEntity entity = EventSimilarityEntity.builder()
                .eventA(eventA)
                .eventB(eventB)
                .score(score)
                .updatedAt(timestamp)
                .build();

        repository.save(entity);

        log.info(
                "Создано сходство событий: eventA={}, eventB={}, score={}",
                eventA,
                eventB,
                score
        );
    }
}