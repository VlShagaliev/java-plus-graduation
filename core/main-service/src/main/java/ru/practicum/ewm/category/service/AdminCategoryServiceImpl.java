package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategory) {
        if (categoryRepository.existsByName(newCategory.getName())) {
            throw new ConflictException("The category name already exists");
        }
        final Category saved = categoryRepository.save(CategoryMapper.from(newCategory));
        return CategoryMapper.toDto(saved);
    }

    @Override
    public void delete(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
        if (eventRepository.existsByCategoryId(id)) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto update(long id, NewCategoryDto updatedCategory) {
        final Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        if (updatedCategory.getName() != null && !updatedCategory.getName().equals(category.getName()))
            if (categoryRepository.existsByName(updatedCategory.getName())) {
                throw new ConflictException("The category name already exists");
            }
        category.setName(updatedCategory.getName());
        final Category saved = categoryRepository.save(category);
        return CategoryMapper.toDto(saved);
    }
}