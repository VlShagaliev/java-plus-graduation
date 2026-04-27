package ru.practicum.ewm.event.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlUtils {
    public static String removeTrailingNumberSegment(String url) {
        Objects.requireNonNull(url, "url must not be null");
        return url.replaceAll("/\\d+$", "");
    }
}