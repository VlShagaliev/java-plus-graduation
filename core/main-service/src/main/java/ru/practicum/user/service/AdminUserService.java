package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface AdminUserService {
    List<UserDto> findUsersByIds(Iterable<Long> ids, int from, int size);

    UserDto create(NewUserRequest newUser);

    void delete(long id);
}