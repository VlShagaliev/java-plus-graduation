package ru.practicum.ewm.category.contract;

public interface CategoryExistenceProvider {
    boolean existsById(long categoryId);
}