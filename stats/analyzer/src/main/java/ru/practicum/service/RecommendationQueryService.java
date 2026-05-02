package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.config.RecommendationDefaultsProperties;
import ru.practicum.model.EventSimilarityEntity;
import ru.practicum.model.UserEventInteraction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserEventInteractionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationQueryService {

    private final UserEventInteractionRepository interactionRepository;
    private final EventSimilarityRepository similarityRepository;
    private final RecommendationDefaultsProperties defaultsProperties;

    public List<RecommendationItem> getRecommendationsForUser(long userId, int maxResults) {
        int limit = normalizeMaxResults(maxResults);

        List<UserEventInteraction> interactions = interactionRepository.findAllByUserId(userId);
        if (interactions.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Double> interactedWeights = interactions.stream()
                .collect(Collectors.toMap(UserEventInteraction::getEventId, UserEventInteraction::getWeight));

        Set<Long> interactedEventIds = interactedWeights.keySet();
        Map<Long, ScoreAccumulator> candidateScores = new HashMap<>();

        for (UserEventInteraction interaction : interactions) {
            long baseEventId = interaction.getEventId();
            double userWeight = interaction.getWeight();

            List<EventSimilarityEntity> similarities =
                    similarityRepository.findAllByEventAOrEventB(baseEventId, baseEventId);

            for (EventSimilarityEntity similarity : similarities) {
                long candidateEventId = similarity.getEventA().equals(baseEventId)
                        ? similarity.getEventB()
                        : similarity.getEventA();

                if (interactedEventIds.contains(candidateEventId)) {
                    continue;
                }

                if (similarity.getScore() <= 0.0) {
                    continue;
                }

                ScoreAccumulator accumulator = candidateScores.computeIfAbsent(
                        candidateEventId,
                        id -> new ScoreAccumulator()
                );
                accumulator.add(similarity.getScore(), userWeight);
            }
        }

        return candidateScores.entrySet().stream()
                .filter(entry -> entry.getValue().getDenominator() > 0.0)
                .map(entry -> new RecommendationItem(
                        entry.getKey(),
                        entry.getValue().getNumerator() / entry.getValue().getDenominator()
                ))
                .sorted(Comparator.comparingDouble(RecommendationItem::score).reversed()
                        .thenComparingLong(RecommendationItem::eventId))
                .limit(limit)
                .toList();
    }

    public List<RecommendationItem> getSimilarEvents(long eventId, long userId, int maxResults) {
        int limit = normalizeMaxResults(maxResults);

        Set<Long> seenEventIds = interactionRepository.findAllByUserId(userId).stream()
                .map(UserEventInteraction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarityEntity> similarities = similarityRepository.findAllByEventAOrEventB(eventId, eventId);

        return similarities.stream()
                .map(similarity -> {
                    long otherEventId = similarity.getEventA().equals(eventId)
                            ? similarity.getEventB()
                            : similarity.getEventA();
                    return new RecommendationItem(otherEventId, similarity.getScore());
                })
                .filter(item -> item.eventId() != eventId)
                .filter(item -> !seenEventIds.contains(item.eventId()))
                .sorted(Comparator.comparingDouble(RecommendationItem::score).reversed()
                        .thenComparingLong(RecommendationItem::eventId))
                .limit(limit)
                .toList();
    }

    public List<RecommendationItem> getInteractionsCount(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Double> sums = new HashMap<>();
        for (Long eventId : eventIds) {
            sums.put(eventId, 0.0);
        }

        List<UserEventInteraction> interactions = interactionRepository.findAllByEventIdIn(eventIds);
        for (UserEventInteraction interaction : interactions) {
            sums.merge(interaction.getEventId(), interaction.getWeight(), Double::sum);
        }

        return eventIds.stream()
                .distinct()
                .map(eventId -> new RecommendationItem(eventId, sums.getOrDefault(eventId, 0.0)))
                .toList();
    }

    private int normalizeMaxResults(int requestedMaxResults) {
        return requestedMaxResults > 0 ? requestedMaxResults : defaultsProperties.getMaxResults();
    }

    public record RecommendationItem(long eventId, double score) {
    }

    private static class ScoreAccumulator {
        private double numerator;
        private double denominator;

        public void add(double similarityScore, double userWeight) {
            numerator += similarityScore * userWeight;
            denominator += similarityScore;
        }

        public double getNumerator() {
            return numerator;
        }

        public double getDenominator() {
            return denominator;
        }
    }
}