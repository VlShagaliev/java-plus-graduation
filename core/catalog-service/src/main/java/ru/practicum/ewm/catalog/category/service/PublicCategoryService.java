package ru.practicum.ewm.catalog.category.service;

import ru.practicum.ewm.catalog.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> findAll(int from, int size);

    CategoryDto find(long id);
}