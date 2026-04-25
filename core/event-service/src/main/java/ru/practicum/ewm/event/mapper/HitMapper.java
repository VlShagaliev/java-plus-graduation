package ru.practicum.ewm.event.mapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.HitCreateDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HitMapper {
    public static HitCreateDto buildCreateHit(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip;
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            ip = forwardedFor.split(",")[0].trim();
        } else if (realIp != null && !realIp.isBlank()) {
            ip = realIp.trim();
        } else {
            ip = request.getRemoteAddr();
        }

        return HitCreateDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
    }
}