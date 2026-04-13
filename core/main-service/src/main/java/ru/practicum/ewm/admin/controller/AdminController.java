package ru.practicum.ewm.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.AdminCategoryService;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.AdminCompilationService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.AdminEventService;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.AdminUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.stats.common.Constants.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {
    private final AdminCategoryService categoryService;
    private final AdminUserService userService;
    private final AdminEventService eventService;
    private final AdminCompilationService compilationService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto category) {
        return categoryService.create(category);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long id) {
        categoryService.delete(id);
    }

    @PatchMapping("categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(
            @PathVariable long id,
            @Valid @RequestBody NewCategoryDto category) {
        return categoryService.update(id, category);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findUsers(
            @RequestParam(required = false) Set<Long> ids,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return userService.findUsersByIds(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUser) {
        return userService.create(newUser);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        userService.delete(id);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> findAllEventsByCriteria(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<EventState> states,
            @RequestParam(required = false) Set<Long> categories,
            @DateTimeFormat(pattern = DATE_TIME_FORMAT) @RequestParam(required = false) LocalDateTime rangeStart,
            @DateTimeFormat(pattern = DATE_TIME_FORMAT) @RequestParam(required = false) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size,
            HttpServletRequest request) {
        return eventService.findAllByCriteria(users, states, categories, rangeStart, rangeEnd, from, size, request);
    }

    @PatchMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @PathVariable long id,
            @Valid @RequestBody UpdateEventAdminRequest updatedEvent,
            HttpServletRequest request) {
        return eventService.update(id, updatedEvent, request);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilation) {
        return compilationService.create(newCompilation);
    }

    @PatchMapping("/compilations/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(
            @PathVariable long id,
            @Valid @RequestBody UpdateCompilationRequest request) {
        return compilationService.update(id, request);
    }

    @DeleteMapping("/compilations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long id) {
        compilationService.delete(id);
    }
}