package ru.practicum.service;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface AdminUserService {
    List<UserDto> findUsersByIds(Iterable<Long> ids, int from, int size);

    UserDto create(NewUserRequest newUser);

    void delete(long id);
}