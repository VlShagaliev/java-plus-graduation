package ru.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.service.AggregationService;
import ru.practicum.avro.UserActionAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionListener {

    private final AggregationService aggregationService;

    @KafkaListener(
            topics = "${aggregator.kafka.topic.user-actions}",
            containerFactory = "userActionKafkaListenerContainerFactory"
    )

    public void onUserAction(UserActionAvro action) {
        log.info(
                "Получено действие пользователя из Kafka: userId={}, eventId={}, actionType={}",
                action.getUserId(),
                action.getEventId(),
                action.getActionType()
        );

        aggregationService.process(action);
    }
}