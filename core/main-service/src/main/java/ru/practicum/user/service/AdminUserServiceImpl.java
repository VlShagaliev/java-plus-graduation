package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.CustomUserRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final CustomUserRepository customUserRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUsersByIds(Iterable<Long> ids, int from, int size) {
        return customUserRepository.findAllByIdsOrAll(ids, from, size)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto create(NewUserRequest newUser) {
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new ConflictException("A user with this email already exists");
        }
        final User saved = userRepository.save(UserMapper.from(newUser));
        return UserMapper.toDto(saved);
    }

    @Override
    public void delete(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id=" + id + " was not found");
        }
        userRepository.deleteById(id);
    }
}