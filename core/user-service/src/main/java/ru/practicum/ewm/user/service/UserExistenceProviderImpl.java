package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.repository.UserRepository;

@Component
@Primary
@RequiredArgsConstructor
public class UserExistenceProviderImpl implements
        ru.practicum.ewm.user.contract.UserExistenceProvider,
        ru.practicum.ewm.request.contract.UserExistenceProvider {

    private final UserRepository userRepository;

    @Override
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }
}