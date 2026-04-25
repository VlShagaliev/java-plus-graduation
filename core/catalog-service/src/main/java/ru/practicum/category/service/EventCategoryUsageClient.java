package ru.practicum.category.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        contextId = "eventCategoryUsageClient",
        name = "event-service",
        path = "/internal/events/categories"
)
public interface EventCategoryUsageClient {

    @GetMapping("/{categoryId}/used")
    boolean isCategoryUsed(@PathVariable("categoryId") long categoryId);
}