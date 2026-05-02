package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.practicum.config.AggregatorKafkaProperties;
import ru.practicum.avro.EventSimilarityAvro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityProducer {

    private final KafkaTemplate<Long, EventSimilarityAvro> kafkaTemplate;
    private final AggregatorKafkaProperties kafkaProperties;

    public void sendAll(Collection<EventSimilarityAvro> similarities) {
        if (similarities == null || similarities.isEmpty()) {
            return;
        }

        try {
            List<CompletableFuture<SendResult<Long, EventSimilarityAvro>>> futures =
                    new ArrayList<>(similarities.size());

            for (EventSimilarityAvro similarity : similarities) {
                Long key = similarity.getEventA();
                futures.add(kafkaTemplate.send(kafkaProperties.getEventsSimilarity(), key, similarity));
            }

            kafkaTemplate.flush();

            for (CompletableFuture<SendResult<Long, EventSimilarityAvro>> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }

            log.info("Отправлена пачка сходств событий в Kafka: count={}", similarities.size());
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось отправить сходства событий в Kafka", e);
        }
    }
}