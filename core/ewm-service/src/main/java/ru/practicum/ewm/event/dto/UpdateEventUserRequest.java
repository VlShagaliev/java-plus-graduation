package ru.practicum.ewm.event.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest extends UpdateEventRequest {
    private UserEventAction stateAction;
}