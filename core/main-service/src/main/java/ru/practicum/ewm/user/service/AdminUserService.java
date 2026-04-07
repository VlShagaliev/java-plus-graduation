package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface AdminUserService {
    List<UserDto> findUsersByIds(Iterable<Long> ids, int from, int size);

    UserDto create(NewUserRequest newUser);

    void delete(long id);
}