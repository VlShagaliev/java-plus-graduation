package ru.practicum.ewm.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.PrivateRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}")
public class PrivateRequestController {
    private final PrivateRequestService requestService;

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findEventRequestsByUser(
            @PathVariable long userId,
            @PathVariable long eventId) {
        return requestService.findEventRequestsByUser(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest,
            @PathVariable long userId,
            @PathVariable long eventId) {
        log.info("users/{}/events/{}/requests -> Update participation request status", userId, eventId);
        return requestService.updateRequestStatus(userId, eventId, updateRequest);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findUserRequests(@PathVariable long userId) {
        return requestService.findUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable long userId,
            @RequestParam @Min(value = 1, message = "The eventId must be bigger than or equal to 1") long eventId) {
        log.info("users/{}/requests -> Create participation request", userId);
        final ParticipationRequestDto response = requestService.create(userId, eventId);
        log.info("Request status={}", response.getStatus());
        return response;
    }

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(
            @PathVariable long userId,
            @PathVariable long requestId) {
        return requestService.cancel(userId, requestId);
    }
}