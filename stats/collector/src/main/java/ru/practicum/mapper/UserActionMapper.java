package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.avro.ActionTypeAvro;
import ru.practicum.avro.UserActionAvro;
import ru.practicum.protobuf.collector.ActionTypeProto;
import ru.practicum.protobuf.collector.UserActionProto;

import java.time.Instant;

@Component
public class UserActionMapper {

    public UserActionAvro toAvro(UserActionProto proto) {
        validate(proto);

        return UserActionAvro.newBuilder()
                .setUserId(proto.getUserId())
                .setEventId(proto.getEventId())
                .setActionType(mapActionType(proto.getActionType()))
                .setTimestamp(Instant.ofEpochSecond(
                        proto.getTimestamp().getSeconds(),
                        proto.getTimestamp().getNanos()
                ))
                .build();
    }

    private static ActionTypeAvro mapActionType(ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Неизвестный тип действия");
        };
    }

    private static void validate(UserActionProto proto) {
        if (proto.getUserId() <= 0) {
            throw new IllegalArgumentException("user_id должен быть положительным");
        }
        if (proto.getEventId() <= 0) {
            throw new IllegalArgumentException("event_id должен быть положительным");
        }
        if (!proto.hasTimestamp()) {
            throw new IllegalArgumentException("timestamp обязателен");
        }
    }
}