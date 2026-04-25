package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.request.contract.EventRequestInfo;
import ru.practicum.ewm.request.contract.EventRequestInfoProvider;
import ru.practicum.ewm.request.contract.UserExistenceProvider;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.ParticipationRequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final RequestRepository requestRepository;
    private final EventRequestInfoProvider eventRequestInfoProvider;
    private final UserExistenceProvider userExistenceProvider;

    @Override
    @Transactional
    public ParticipationRequestDto create(long userId, long eventId) {
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException(
                    "The user request with id=" + userId + " for event with id=" + eventId + " already exists"
            );
        }

        final EventRequestInfo event = eventRequestInfoProvider.getEventRequestInfo(eventId);

        if (!userExistenceProvider.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        if (event.getInitiatorId() == userId) {
            throw new ConflictException("The user cannot submit participation requests for their own event");
        }
        if (!event.isPublished()) {
            throw new ConflictException("The user cannot submit participation requests for unpublished events");
        }

        if (!event.isRequestModeration()) {
            final long participants = requestRepository.countByEventIdAndStatusNot(eventId, RequestStatus.REJECTED);
            if (event.getParticipantLimit() == participants) {
                throw new ConflictException("The max number of participants for this event has been reached");
            }
        }

        final Request request = RequestMapper.toRequest(userId, eventId);
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(long userId, long requestId) {
        final Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> findUserRequests(long userId) {
        if (!userExistenceProvider.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public List<ParticipationRequestDto> findEventRequestsByUser(long userId, long eventId) {
        final EventRequestInfo event = eventRequestInfoProvider.getEventRequestInfo(eventId);

        if (event.getInitiatorId() != userId) {
            throw new NotFoundException("Event with id=" + eventId + " was not found for user with id=" + userId);
        }

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest updatedRequest) {
        final EventRequestInfo event = eventRequestInfoProvider.getEventRequestInfo(eventId);

        if (event.getInitiatorId() != userId) {
            throw new NotFoundException("Event with id=" + eventId + " was not found for user with id=" + userId);
        }

        long participants = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && participants == event.getParticipantLimit()) {
            throw new ConflictException("The max number of participants for this event has been reached");
        }

        final EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .rejectedRequests(new ArrayList<>())
                .confirmedRequests(new ArrayList<>())
                .build();

        for (Long id : updatedRequest.getRequestIds()) {
            Request request = requestRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Request with id=" + id + " was not found"));

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("It is not possible to change the request status");
            }

            request.setStatus(
                    updatedRequest.getStatus() == ParticipationRequestStatus.REJECTED
                            ? RequestStatus.REJECTED
                            : RequestStatus.CONFIRMED
            );

            if (participants++ == event.getParticipantLimit()) {
                requestRepository.updateStatusesByEventAndCurrentStatus(
                        RequestStatus.PENDING,
                        RequestStatus.REJECTED,
                        eventId
                );
                break;
            }

            final Request saved = requestRepository.save(request);
            final ParticipationRequestDto dto = RequestMapper.toParticipationRequestDto(saved);
            if (saved.getStatus() == RequestStatus.CONFIRMED) {
                result.getConfirmedRequests().add(dto);
            } else {
                result.getRejectedRequests().add(dto);
            }
        }

        return result;
    }
}