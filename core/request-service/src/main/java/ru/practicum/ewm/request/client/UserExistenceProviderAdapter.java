package ru.practicum.ewm.request.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.contract.UserExistenceProvider;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserExistenceProviderAdapter implements UserExistenceProvider {

    private final UserInternalClient userInternalClient;

    @Override
    public boolean existsById(long userId) {
        try {
            return userInternalClient.existsById(userId);
        } catch (RuntimeException ex) {
            log.warn("Failed to check user existence for userId={}, returning false", userId, ex);
            return false;
        }
    }
}