package ru.practicum.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private ParticipationRequestStatus status;
}