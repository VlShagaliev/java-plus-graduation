package ru.practicum.ewm.user.contract;

public interface UserExistenceProvider {
    boolean existsById(long userId);
}