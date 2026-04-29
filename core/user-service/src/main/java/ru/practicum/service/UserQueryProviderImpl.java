package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.dto.UserShortInfo;
import ru.practicum.user.contract.UserExistenceProvider;
import ru.practicum.user.contract.UserShortInfoProvider;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserQueryProviderImpl implements UserExistenceProvider, UserShortInfoProvider {
    private final UserRepository userRepository;

    @Override
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserShortInfo getShortInfo(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        return toShortInfo(user);
    }

    @Override
    public Map<Long, UserShortInfo> getShortInfoByIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        UserQueryProviderImpl::toShortInfo,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private static UserShortInfo toShortInfo(User user) {
        return UserShortInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}