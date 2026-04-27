package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.EventShortInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    private List<EventShortInfo> events;
}