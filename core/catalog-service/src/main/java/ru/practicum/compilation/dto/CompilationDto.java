package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.dto.EventShortInfo;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    private List<EventShortInfo> events;
}