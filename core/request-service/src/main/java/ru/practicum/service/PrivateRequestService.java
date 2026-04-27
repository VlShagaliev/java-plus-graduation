package ru.practicum.service;

import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {
    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto cancel(long userId, long requestId);

    List<ParticipationRequestDto> findUserRequests(long userId);

    List<ParticipationRequestDto> findEventRequestsByUser(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request
    );
}