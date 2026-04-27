package ru.practicum.ewm.catalog.category.service;

import ru.practicum.ewm.catalog.category.dto.CategoryDto;
import ru.practicum.ewm.catalog.category.dto.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto create(NewCategoryDto newCategory);

    void delete(long id);

    CategoryDto update(long id, NewCategoryDto updatedCategory);
}