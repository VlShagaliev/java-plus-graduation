package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEventId())
                .created(request.getCreated())
                .requester(request.getRequesterId())
                .status(request.getStatus())
                .build();
    }

    public static Request toRequest(long requesterId, long eventId) {
        return Request.builder()
                .requesterId(requesterId)
                .eventId(eventId)
                .status(RequestStatus.PENDING)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .build();
    }
}