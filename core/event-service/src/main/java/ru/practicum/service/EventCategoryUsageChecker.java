package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.category.contract.CategoryUsageChecker;
import ru.practicum.repository.EventRepository;

@Component
@Primary
@RequiredArgsConstructor
public class EventCategoryUsageChecker implements CategoryUsageChecker {
    private final EventRepository eventRepository;

    @Override
    public boolean isCategoryUsed(long categoryId) {
        return eventRepository.existsByCategoryId(categoryId);
    }
}