package ru.practicum.user.contract;

public interface UserExistenceProvider {
    boolean existsById(long userId);
}