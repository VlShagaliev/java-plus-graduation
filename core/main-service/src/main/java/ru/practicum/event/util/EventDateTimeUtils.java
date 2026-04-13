package ru.practicum.event.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventDateTimeUtils {
    private static final int YEARS_OFFSET = 100;

    public static LocalDateTime defaultStart() {
        return LocalDateTime.now().minusYears(YEARS_OFFSET);
    }

    public static LocalDateTime defaultEnd() {
        return LocalDateTime.now().plusYears(YEARS_OFFSET);
    }
}