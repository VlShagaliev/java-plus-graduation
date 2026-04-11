package ru.practicum.common.config.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.event.dto.EventSortOption;

public class StringToEventSortOptionConverter implements Converter<String, EventSortOption> {
    @Override
    public EventSortOption convert(String source) {
        return EventSortOption.valueOf(source.toUpperCase());
    }
}