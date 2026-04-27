package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto create(NewCategoryDto newCategory);

    void delete(long id);

    CategoryDto update(long id, NewCategoryDto updatedCategory);
}