package ru.practicum.ewm.catalog.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.catalog.category.dto.CategoryDto;
import ru.practicum.ewm.catalog.category.mapper.CategoryMapper;
import ru.practicum.ewm.catalog.category.model.Category;
import ru.practicum.ewm.catalog.category.repository.CategoryRepository;
import ru.practicum.ewm.common.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAll(int from, int size) {
        int page = from / size;

        return categoryRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto find(long id) {
        final Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        return CategoryMapper.toDto(category);
    }
}