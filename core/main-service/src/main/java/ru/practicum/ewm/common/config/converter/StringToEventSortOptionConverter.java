package ru.practicum.ewm.common.config.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.ewm.event.dto.EventSortOption;

public class StringToEventSortOptionConverter implements Converter<String, EventSortOption> {
    @Override
    public EventSortOption convert(String source) {
        return EventSortOption.valueOf(source.toUpperCase());
    }
}