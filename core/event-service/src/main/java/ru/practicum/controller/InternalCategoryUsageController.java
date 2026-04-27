package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.contract.CategoryUsageChecker;

@RestController
@RequestMapping("/internal/events/categories")
@RequiredArgsConstructor
public class InternalCategoryUsageController {

    private final CategoryUsageChecker categoryUsageChecker;

    @GetMapping("/{categoryId}/used")
    public boolean isCategoryUsed(@PathVariable long categoryId) {
        return categoryUsageChecker.isCategoryUsed(categoryId);
    }
}