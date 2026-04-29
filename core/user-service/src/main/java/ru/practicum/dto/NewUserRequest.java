package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.common.annotation.SplitEmailValidation;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @SplitEmailValidation
    @Size(min = 6, max = 254, message = "The email address must be between 6 and 64 characters long")
    private String email;

    @NotBlank
    @Size(min = 2, max = 250, message = "The name must be between 2 and 250 characters long")
    private String name;
}