package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.avro.EventSimilarityAvro;
import ru.practicum.avro.UserActionAvro;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {

    private final ActionWeightResolver actionWeightResolver;
    private final EventSimilarityProducer eventSimilarityProducer;

    private final Map<Long, Map<Long, Double>> eventUserWeights = new HashMap<>();

    private final Map<Long, Double> eventWeightSums = new HashMap<>();

    private final Map<Long, Map<Long, Double>> minWeightSums = new HashMap<>();

    public synchronized void process(UserActionAvro action) {
        if (action == null) {
            return;
        }

        long eventId = action.getEventId();
        long userId = action.getUserId();
        double incomingWeight = actionWeightResolver.resolve(action.getActionType());

        Map<Long, Double> usersWeights = eventUserWeights.computeIfAbsent(eventId, id -> new HashMap<>());
        double oldWeight = usersWeights.getOrDefault(userId, 0.0);

        if (incomingWeight <= oldWeight) {
            log.info(
                    "Вес не изменился, пересчет не требуется: userId={}, eventId={}, oldWeight={}, incomingWeight={}",
                    userId,
                    eventId,
                    oldWeight,
                    incomingWeight
            );
            return;
        }

        usersWeights.put(userId, incomingWeight);

        double deltaWeight = incomingWeight - oldWeight;
        double updatedEventSum = eventWeightSums.getOrDefault(eventId, 0.0) + deltaWeight;
        eventWeightSums.put(eventId, updatedEventSum);

        List<EventSimilarityAvro> similaritiesToSend = new ArrayList<>();

        for (Map.Entry<Long, Map<Long, Double>> entry : eventUserWeights.entrySet()) {
            long otherEventId = entry.getKey();
            if (otherEventId == eventId) {
                continue;
            }

            Double otherWeight = entry.getValue().get(userId);
            if (otherWeight == null) {
                continue;
            }

            double oldMin = Math.min(oldWeight, otherWeight);
            double newMin = Math.min(incomingWeight, otherWeight);
            double deltaMin = newMin - oldMin;

            double updatedMinSum = addToMinWeightSum(eventId, otherEventId, deltaMin);
            double otherEventSum = eventWeightSums.getOrDefault(otherEventId, 0.0);
            double similarityScore = calculateSimilarity(updatedMinSum, updatedEventSum, otherEventSum);

            similaritiesToSend.add(buildSimilarityMessage(
                    eventId,
                    otherEventId,
                    similarityScore,
                    action.getTimestamp()
            ));
        }

        eventSimilarityProducer.sendAll(similaritiesToSend);
    }

    private double calculateSimilarity(double minSum, double eventSumA, double eventSumB) {
        if (eventSumA <= 0.0 || eventSumB <= 0.0) {
            return 0.0;
        }
        return minSum / Math.sqrt(eventSumA * eventSumB);
    }

    private double addToMinWeightSum(long eventIdA, long eventIdB, double delta) {
        long first = Math.min(eventIdA, eventIdB);
        long second = Math.max(eventIdA, eventIdB);

        Map<Long, Double> secondMap = minWeightSums.computeIfAbsent(first, id -> new HashMap<>());
        double updatedValue = secondMap.getOrDefault(second, 0.0) + delta;
        secondMap.put(second, updatedValue);

        return updatedValue;
    }

    private EventSimilarityAvro buildSimilarityMessage(
            long eventIdA,
            long eventIdB,
            double score,
            Instant timestamp
    ) {
        long first = Math.min(eventIdA, eventIdB);
        long second = Math.max(eventIdA, eventIdB);

        return EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(score)
                .setTimestamp(timestamp)
                .build();
    }
}