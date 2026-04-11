package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.common.Constants.DATE_TIME_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private long id;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    private EventState state;
    private String title;
    private long views;
    private String modsComment;
}
