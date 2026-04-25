package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.category.contract.CategoryExistenceProvider;
import ru.practicum.category.contract.CategoryShortInfoProvider;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.api.dto.CategoryShortInfo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class CategoryQueryProviderImpl implements CategoryExistenceProvider, CategoryShortInfoProvider {
    private final CategoryRepository categoryRepository;

    @Override
    public boolean existsById(long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    public CategoryShortInfo getShortInfo(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));

        return toShortInfo(category);
    }

    @Override
    public Map<Long, CategoryShortInfo> getShortInfoByIds(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Map.of();
        }

        return categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        CategoryQueryProviderImpl::toShortInfo,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private static CategoryShortInfo toShortInfo(Category category) {
        return CategoryShortInfo.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}