package ru.practicum.ewm.event.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.contract.CategoryExistenceProvider;
import ru.practicum.ewm.category.contract.CategoryShortInfoProvider;
import ru.practicum.ewm.event.api.dto.CategoryShortInfo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryProviderAdapter implements CategoryExistenceProvider, CategoryShortInfoProvider {
    private final CategoryInternalClient categoryInternalClient;

    @Override
    public boolean existsById(long categoryId) {
        try {
            return categoryInternalClient.existsById(categoryId);
        } catch (RuntimeException ex) {
            log.warn("Failed to check category existence for categoryId={}, returning false", categoryId, ex);
            return false;
        }
    }

    @Override
    public CategoryShortInfo getShortInfo(long categoryId) {
        try {
            return categoryInternalClient.getShortInfo(categoryId);
        } catch (RuntimeException ex) {
            log.warn("Failed to get category short info for categoryId={}, returning fallback", categoryId, ex);
            return fallbackCategory(categoryId);
        }
    }

    @Override
    public Map<Long, CategoryShortInfo> getShortInfoByIds(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Map.of();
        }

        try {
            Map<Long, CategoryShortInfo> result = categoryInternalClient.getShortInfoByIds(categoryIds);
            return result == null ? buildFallbackCategories(categoryIds) : fillMissingCategories(categoryIds, result);
        } catch (RuntimeException ex) {
            log.warn("Failed to get categories short info batch, returning fallback for ids={}", categoryIds, ex);
            return buildFallbackCategories(categoryIds);
        }
    }

    private static Map<Long, CategoryShortInfo> buildFallbackCategories(Collection<Long> categoryIds) {
        Map<Long, CategoryShortInfo> result = new LinkedHashMap<>();
        for (Long categoryId : categoryIds) {
            result.put(categoryId, fallbackCategory(categoryId));
        }
        return result;
    }

    private static Map<Long, CategoryShortInfo> fillMissingCategories(
            Collection<Long> categoryIds,
            Map<Long, CategoryShortInfo> source
    ) {
        Map<Long, CategoryShortInfo> result = new LinkedHashMap<>();
        for (Long categoryId : categoryIds) {
            result.put(categoryId, source.getOrDefault(categoryId, fallbackCategory(categoryId)));
        }
        return result;
    }

    private static CategoryShortInfo fallbackCategory(long categoryId) {
        return CategoryShortInfo.builder()
                .id(categoryId)
                .name("unknown category")
                .build();
    }
}