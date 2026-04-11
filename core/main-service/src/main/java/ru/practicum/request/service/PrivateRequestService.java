package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

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