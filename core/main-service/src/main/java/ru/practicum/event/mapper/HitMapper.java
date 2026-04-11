package ru.practicum.event.mapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.HitCreateDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HitMapper {
    public static HitCreateDto buildCreateHit(HttpServletRequest request) {
        return HitCreateDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}