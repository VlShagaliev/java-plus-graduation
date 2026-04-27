package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.api.dto.UserShortInfo;
import ru.practicum.ewm.user.service.UserQueryProviderImpl;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserQueryProviderImpl userQueryProvider;

    @GetMapping("/{userId}/exists")
    public boolean existsById(@PathVariable long userId) {
        return userQueryProvider.existsById(userId);
    }

    @GetMapping("/{userId}/short")
    public UserShortInfo getShortInfo(@PathVariable long userId) {
        return userQueryProvider.getShortInfo(userId);
    }

    @PostMapping("/short/batch")
    public Map<Long, UserShortInfo> getShortInfoByIds(@RequestBody Collection<Long> userIds) {
        return userQueryProvider.getShortInfoByIds(userIds);
    }
}