package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.UserEventInteraction;
import ru.practicum.repository.UserEventInteractionRepository;
import ru.practicum.avro.UserActionAvro;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionUpdateService {

    private final UserEventInteractionRepository repository;
    private final ActionWeightResolver actionWeightResolver;

    @Transactional
    public void process(UserActionAvro action) {
        if (action == null) {
            return;
        }

        long userId = action.getUserId();
        long eventId = action.getEventId();
        double incomingWeight = actionWeightResolver.resolve(action.getActionType());
        Instant timestamp = action.getTimestamp() == null ? Instant.now() : action.getTimestamp();

        repository.findByUserIdAndEventId(userId, eventId)
                .ifPresentOrElse(
                        existing -> updateExisting(existing, incomingWeight, timestamp),
                        () -> createNew(userId, eventId, incomingWeight, timestamp)
                );
    }

    private void updateExisting(UserEventInteraction existing, double incomingWeight, Instant timestamp) {
        if (incomingWeight > existing.getWeight()) {
            existing.setWeight(incomingWeight);
            existing.setUpdatedAt(timestamp);
            repository.save(existing);

            log.info(
                    "Обновлено взаимодействие пользователя: userId={}, eventId={}, weight={}",
                    existing.getUserId(),
                    existing.getEventId(),
                    existing.getWeight()
            );
        } else {
            log.info(
                    "Вес взаимодействия не изменился: userId={}, eventId={}, oldWeight={}, incomingWeight={}",
                    existing.getUserId(),
                    existing.getEventId(),
                    existing.getWeight(),
                    incomingWeight
            );
        }
    }

    private void createNew(long userId, long eventId, double weight, Instant timestamp) {
        UserEventInteraction interaction = UserEventInteraction.builder()
                .userId(userId)
                .eventId(eventId)
                .weight(weight)
                .updatedAt(timestamp)
                .build();

        repository.save(interaction);

        log.info(
                "Создано новое взаимодействие пользователя: userId={}, eventId={}, weight={}",
                userId,
                eventId,
                weight
        );
    }
}