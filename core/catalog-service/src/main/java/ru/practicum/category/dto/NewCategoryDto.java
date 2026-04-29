package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "The category name must not be empty")
    @Length(max = 50, message = "The category name must not exceed 50 characters")
    private String name;
}