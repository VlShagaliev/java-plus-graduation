package ru.practicum.ewm.catalog.category.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.catalog.category.dto.CategoryDto;
import ru.practicum.ewm.catalog.category.dto.NewCategoryDto;
import ru.practicum.ewm.catalog.category.model.Category;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category from(NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }
}