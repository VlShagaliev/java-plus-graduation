package ru.practicum.ewm.event.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.api.dto.UserShortInfo;
import ru.practicum.ewm.user.contract.UserExistenceProvider;
import ru.practicum.ewm.user.contract.UserShortInfoProvider;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProviderAdapter implements UserExistenceProvider, UserShortInfoProvider {
    private final UserInternalClient userInternalClient;

    @Override
    public boolean existsById(long userId) {
        return safeCall(
                () -> userInternalClient.existsById(userId),
                false,
                "Failed to check user existence for userId={}",
                userId
        );
    }

    @Override
    public UserShortInfo getShortInfo(long userId) {
        return safeCall(
                () -> userInternalClient.getShortInfo(userId),
                fallbackUser(userId),
                "Failed to get user short info for userId={}",
                userId
        );
    }

    @Override
    public Map<Long, UserShortInfo> getShortInfoByIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, UserShortInfo> fallback = buildFallbackUsers(userIds);

        Map<Long, UserShortInfo> result = safeCall(
                () -> userInternalClient.getShortInfoByIds(userIds),
                fallback,
                "Failed to get users short info batch for ids={}",
                userIds
        );

        return result == null ? fallback : fillMissingUsers(userIds, result);
    }

    private <T> T safeCall(Supplier<T> supplier, T fallback, String message, Object arg) {
        try {
            return supplier.get();
        } catch (RuntimeException ex) {
            log.warn(message, arg, ex);
            return fallback;
        }
    }

    private static Map<Long, UserShortInfo> buildFallbackUsers(Collection<Long> userIds) {
        Map<Long, UserShortInfo> result = new LinkedHashMap<>();
        for (Long userId : userIds) {
            result.put(userId, fallbackUser(userId));
        }
        return result;
    }

    private static Map<Long, UserShortInfo> fillMissingUsers(
            Collection<Long> userIds,
            Map<Long, UserShortInfo> source
    ) {
        Map<Long, UserShortInfo> result = new LinkedHashMap<>();
        for (Long userId : userIds) {
            result.put(userId, source.getOrDefault(userId, fallbackUser(userId)));
        }
        return result;
    }

    private static UserShortInfo fallbackUser(long userId) {
        return UserShortInfo.builder()
                .id(userId)
                .name("unknown user")
                .build();
    }
}