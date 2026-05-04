package ru.practicum.grpc;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.protobuf.collector.ActionTypeProto;
import ru.practicum.protobuf.collector.UserActionControllerGrpc;
import ru.practicum.protobuf.collector.UserActionProto;

import java.time.Instant;

@Slf4j
@Component
public class CollectorGrpcClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorStub;

    public void sendViewAction(long userId, long eventId) {
        sendActionSafely(userId, eventId, ActionTypeProto.ACTION_VIEW);
    }

    public void sendRegisterAction(long userId, long eventId) {
        sendActionSafely(userId, eventId, ActionTypeProto.ACTION_REGISTER);
    }

    public void sendLikeAction(long userId, long eventId) {
        sendActionSafely(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }

    private void sendActionSafely(long userId, long eventId, ActionTypeProto actionType) {
        try {
            sendAction(userId, eventId, actionType);
        } catch (RuntimeException ex) {
            log.warn(
                    "Не удалось отправить действие {} в collector: userId={}, eventId={}",
                    actionType,
                    userId,
                    eventId,
                    ex
            );
        }
    }

    private void sendAction(long userId, long eventId, ActionTypeProto actionType) {
        Instant now = Instant.now();

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(timestamp)
                .build();

        collectorStub.collectUserAction(request);

        log.info(
                "Отправлено действие {} в collector: userId={}, eventId={}",
                actionType,
                userId,
                eventId
        );
    }
}