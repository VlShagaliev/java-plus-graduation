package ru.practicum.event.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlUtils {
    public static String removeTrailingNumberSegment(String url) {
        if (url == null) {
            return null;
        }
        return url.replaceAll("/\\d+$", "");
    }
}