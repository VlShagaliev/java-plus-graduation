package ru.practicum.ewm.request.contract;

public interface UserExistenceProvider {
    boolean existsById(long userId);
}