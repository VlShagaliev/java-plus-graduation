package ru.practicum.ewm.catalog.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.catalog.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}