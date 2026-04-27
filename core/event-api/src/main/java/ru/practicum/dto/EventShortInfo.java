package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortInfo {
    private long id;
    private String title;
    private String annotation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private boolean paid;
    private long views;
    private long confirmedRequests;
    private CategoryShortInfo category;
    private UserShortInfo initiator;
}