package ru.practicum.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.service.UserActionProducer;
import ru.practicum.avro.UserActionAvro;
import ru.practicum.protobuf.collector.UserActionControllerGrpc;
import ru.practicum.protobuf.collector.UserActionProto;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserActionControllerGrpcService extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionMapper userActionMapper;
    private final UserActionProducer userActionProducer;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            UserActionAvro actionAvro = userActionMapper.toAvro(request);
            userActionProducer.send(actionAvro);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.warn(
                    "Некорректный запрос в Collector: userId={}, eventId={}, actionType={}, hasTimestamp={}, error={}",
                    request.getUserId(),
                    request.getEventId(),
                    request.getActionType(),
                    request.hasTimestamp(),
                    e.getMessage()
            );
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        } catch (Exception e) {
            log.error(
                    "Ошибка обработки действия пользователя в Collector: userId={}, eventId={}, actionType={}, hasTimestamp={}",
                    request.getUserId(),
                    request.getEventId(),
                    request.getActionType(),
                    request.hasTimestamp(),
                    e
            );
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Не удалось обработать действие пользователя")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}