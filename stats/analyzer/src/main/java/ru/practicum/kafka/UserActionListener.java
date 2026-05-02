package ru.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.service.InteractionUpdateService;
import ru.practicum.avro.UserActionAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionListener {

    private final InteractionUpdateService interactionUpdateService;

    @KafkaListener(
            topics = "${analyzer.kafka.topic.user-actions}",
            containerFactory = "userActionKafkaListenerContainerFactory"
    )
    public void onUserAction(UserActionAvro action) {
        if (action == null) {
            log.warn("Получено пустое сообщение о действии пользователя");
            return;
        }

        interactionUpdateService.process(action);
    }
}