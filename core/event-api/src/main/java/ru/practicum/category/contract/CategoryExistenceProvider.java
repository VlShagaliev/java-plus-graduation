package ru.practicum.category.contract;

public interface CategoryExistenceProvider {
    boolean existsById(long categoryId);
}