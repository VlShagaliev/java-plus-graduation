package ru.practicum.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.common.exception.BadRequestException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.common.Constants.DATE_TIME_FORMAT;

@ControllerAdvice
public final class GlobalExceptionHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return buildBadRequest(e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return buildBadRequest(e);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException e) {
        return buildBadRequest(e);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        final ApiError error = ApiError.builder()
                .status("NOT_FOUND")
                .reason("The required object was not found")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException e) {
        final ApiError error = ApiError.builder()
                .status("CONFLICT")
                .reason("For the requested operation the conditions are not met")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(409).body(error);
    }

    private static String getCurrentTimestampAsString() {
        return LocalDateTime.now().format(FORMATTER);
    }

    private static ResponseEntity<ApiError> buildBadRequest(Throwable e) {
        final ApiError error = ApiError.builder()
                .status("BAD_REQUEST")
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(400).body(error);
    }
}