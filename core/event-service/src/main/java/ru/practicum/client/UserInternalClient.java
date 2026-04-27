package ru.practicum.ewm.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.api.dto.UserShortInfo;

import java.util.Collection;
import java.util.Map;

@FeignClient(name = "user-service", path = "/internal/users")
public interface UserInternalClient {
    @GetMapping("/{userId}/exists")
    boolean existsById(@PathVariable("userId") long userId);

    @GetMapping("/{userId}/short")
    UserShortInfo getShortInfo(@PathVariable("userId") long userId);

    @PostMapping("/short/batch")
    Map<Long, UserShortInfo> getShortInfoByIds(@RequestBody Collection<Long> userIds);
}