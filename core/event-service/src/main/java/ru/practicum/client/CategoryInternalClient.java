package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryShortInfo;

import java.util.Collection;
import java.util.Map;

@FeignClient(name = "event-service", path = "/internal/categories")
public interface CategoryInternalClient {
    @GetMapping("/{categoryId}/exists")
    boolean existsById(@PathVariable("categoryId") long categoryId);

    @GetMapping("/{categoryId}/short")
    CategoryShortInfo getShortInfo(@PathVariable("categoryId") long categoryId);

    @PostMapping("/short/batch")
    Map<Long, CategoryShortInfo> getShortInfoByIds(@RequestBody Collection<Long> categoryIds);
}