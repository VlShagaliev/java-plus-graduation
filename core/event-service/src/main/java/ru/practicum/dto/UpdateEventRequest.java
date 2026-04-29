package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.common.Constants.DATE_TIME_FORMAT;

@Data
@NoArgsConstructor
public abstract class UpdateEventRequest {
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @FutureOrPresent
    private LocalDateTime eventDate;

    private Location location;
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @Length(min = 3, max = 120)
    private String title;

    private String modsComment;
}