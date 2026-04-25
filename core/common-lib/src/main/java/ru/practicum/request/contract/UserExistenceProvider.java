package ru.practicum.request.contract;

public interface UserExistenceProvider {
    boolean existsById(long userId);
}