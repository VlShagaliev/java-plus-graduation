package ru.practicum.grpc;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.stats.protobuf.dashboard.*;
import ru.practicum.dto.RecommendedEvent;

import java.util.*;

@Slf4j
@Component
public class AnalyzerGrpcClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerStub;

    public List<RecommendedEvent> getRecommendationsForUser(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        List<RecommendedEvent> result = new ArrayList<>();
        analyzerStub.getRecommendationsForUser(request)
                .forEachRemaining(item -> result.add(toDto(item)));

        return result;
    }

    public List<RecommendedEvent> getSimilarEvents(long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        List<RecommendedEvent> result = new ArrayList<>();
        analyzerStub.getSimilarEvents(request)
                .forEachRemaining(item -> result.add(toDto(item)));

        return result;
    }

    public Map<Long, Double> getInteractionsCount(Collection<Long> eventIds) {
        InteractionsCountRequestProto.Builder builder = InteractionsCountRequestProto.newBuilder();
        builder.addAllEventId(eventIds);

        Map<Long, Double> result = new LinkedHashMap<>();
        analyzerStub.getInteractionsCount(builder.build())
                .forEachRemaining(item -> result.put(item.getEventId(), item.getScore()));

        return result;
    }

    private static RecommendedEvent toDto(RecommendedEventProto proto) {
        return new RecommendedEvent(proto.getEventId(), proto.getScore());
    }
}