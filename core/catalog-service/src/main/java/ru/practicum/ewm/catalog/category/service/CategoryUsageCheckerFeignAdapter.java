package ru.practicum.ewm.catalog.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.contract.CategoryUsageChecker;

@Component
@RequiredArgsConstructor
public class CategoryUsageCheckerFeignAdapter implements CategoryUsageChecker {

    private final EventCategoryUsageClient eventCategoryUsageClient;

    @Override
    public boolean isCategoryUsed(long categoryId) {
        return eventCategoryUsageClient.isCategoryUsed(categoryId);
    }
}