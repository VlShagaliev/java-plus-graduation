package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.ParticipationRequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(long userId, long eventId) {
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException(
                    "The user request with id=" + userId + " for event with id=" + eventId + " already exists"
            );
        }

        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        final User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        if (event.getInitiator().getId() == requestor.getId()) {
            throw new ConflictException("The user cannot submit participation requests for their own event");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("The user cannot submit participation requests for unpublished events");
        }

        if (!event.isRequestModeration()) {
            final long participants = requestRepository.countByEventIdAndStatusNot(eventId, RequestStatus.REJECTED);
            if (event.getParticipantLimit() == participants) {
                throw new ConflictException("The max number of participants for this event has been reached");
            }
        }

        final Request request = RequestMapper.toRequest(requestor, event);
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
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public List<ParticipationRequestDto> findEventRequestsByUser(long userId, long eventId) {
        return requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId)
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
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

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
                    updatedRequest.getStatus() == ParticipationRequestStatus.REJECTED ?
                            RequestStatus.REJECTED :
                            RequestStatus.CONFIRMED
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
            final ParticipationRequestDto dto = RequestMapper.toParticipationRequestDto(request);
            if (saved.getStatus() == RequestStatus.CONFIRMED) {
                result.getConfirmedRequests().add(dto);
            } else {
                result.getRejectedRequests().add(dto);
            }
        }

        return result;
    }
}