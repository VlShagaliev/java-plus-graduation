package ru.practicum.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.api.dto.CategoryShortInfo;
import ru.practicum.event.api.dto.UserShortInfo;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryShortInfo category;
    private boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortInfo initiator;
    private Location location;
    private int participantLimit;
    private boolean requestModeration;
    private EventState state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private long views;
    private long confirmedRequests;
    private String modsComment;
}