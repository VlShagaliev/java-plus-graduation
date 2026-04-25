package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.contract.CategoryExistenceProvider;
import ru.practicum.category.contract.CategoryShortInfoProvider;
import ru.practicum.event.api.dto.CategoryShortInfo;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/internal/categories")
@RequiredArgsConstructor
public class InternalCategoryController {
    private final CategoryExistenceProvider categoryExistenceProvider;
    private final CategoryShortInfoProvider categoryShortInfoProvider;

    @GetMapping("/{categoryId}/exists")
    public boolean existsById(@PathVariable long categoryId) {
        return categoryExistenceProvider.existsById(categoryId);
    }

    @GetMapping("/{categoryId}/short")
    public CategoryShortInfo getShortInfo(@PathVariable long categoryId) {
        return categoryShortInfoProvider.getShortInfo(categoryId);
    }

    @PostMapping("/short/batch")
    public Map<Long, CategoryShortInfo> getShortInfoByIds(@RequestBody Collection<Long> categoryIds) {
        return categoryShortInfoProvider.getShortInfoByIds(categoryIds);
    }
}