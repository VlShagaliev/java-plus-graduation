package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.repository.UserRepository;
import ru.practicum.request.contract.UserExistenceProvider;

@Component
@Primary
@RequiredArgsConstructor
public class UserExistenceProviderImpl implements
        ru.practicum.user.contract.UserExistenceProvider,
        UserExistenceProvider {

    private final UserRepository userRepository;

    @Override
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }
}