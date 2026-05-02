package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollectorRequestGrpcClient {

    private final ru.practicum.grpc.CollectorGrpcClient delegate;

    public void sendRegisterAction(long userId, long eventId) {
        delegate.sendRegisterAction(userId, eventId);
    }
}