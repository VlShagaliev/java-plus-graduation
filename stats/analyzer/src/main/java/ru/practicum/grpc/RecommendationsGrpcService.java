package ru.practicum.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.service.RecommendationQueryService;
import ru.practicum.protobuf.dashboard.*;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcService extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationQueryService recommendationQueryService;

    @Override
    public void getRecommendationsForUser(
            UserPredictionsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver
    ) {
        try {
            validatePositive(request.getUserId(), "user_id должен быть положительным");

            recommendationQueryService.getRecommendationsForUser(request.getUserId(), request.getMaxResults())
                    .forEach(item -> responseObserver.onNext(toProto(item)));

            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        } catch (Exception e) {
            log.error("Ошибка получения рекомендаций для пользователя", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Не удалось получить рекомендации для пользователя")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getSimilarEvents(
            SimilarEventsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver
    ) {
        try {
            validatePositive(request.getEventId(), "event_id должен быть положительным");
            validatePositive(request.getUserId(), "user_id должен быть положительным");

            recommendationQueryService.getSimilarEvents(
                            request.getEventId(),
                            request.getUserId(),
                            request.getMaxResults()
                    )
                    .forEach(item -> responseObserver.onNext(toProto(item)));

            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        } catch (Exception e) {
            log.error("Ошибка получения похожих событий", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Не удалось получить похожие события")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getInteractionsCount(
            InteractionsCountRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver
    ) {
        try {
            recommendationQueryService.getInteractionsCount(request.getEventIdList())
                    .forEach(item -> responseObserver.onNext(toProto(item)));

            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка получения суммы взаимодействий", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Не удалось получить сумму взаимодействий")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }

    private static RecommendedEventProto toProto(RecommendationQueryService.RecommendationItem item) {
        return RecommendedEventProto.newBuilder()
                .setEventId(item.eventId())
                .setScore(item.score())
                .build();
    }

    private static void validatePositive(long value, String message) {
        if (value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}