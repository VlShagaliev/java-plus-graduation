package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryShortInfo {
    private long id;
    private String name;
}