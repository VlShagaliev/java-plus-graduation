package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.config.CollectorKafkaProperties;
import ru.practicum.avro.UserActionAvro;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionProducer {

    private final KafkaTemplate<Long, UserActionAvro> kafkaTemplate;
    private final CollectorKafkaProperties collectorKafkaProperties;

    public void send(UserActionAvro action) {
        try {
            Long key = action.getUserId();

            kafkaTemplate.send(collectorKafkaProperties.getUserActions(), key, action)
                    .get(10, TimeUnit.SECONDS);

            log.info(
                    "Отправлено действие пользователя в Kafka: userId={}, eventId={}, actionType={}",
                    action.getUserId(),
                    action.getEventId(),
                    action.getActionType()
            );
        } catch (Exception e) {
            log.error(
                    "Ошибка отправки действия пользователя в Kafka: topic={}, key={}, userId={}, eventId={}, actionType={}",
                    collectorKafkaProperties.getUserActions(),
                    action.getUserId(),
                    action.getUserId(),
                    action.getEventId(),
                    action.getActionType(),
                    e
            );
            throw new IllegalStateException("Не удалось отправить действие пользователя в Kafka", e);
        }
    }
}