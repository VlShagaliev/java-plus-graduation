package ru.practicum.common.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

@ControllerAdvice
public final class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation error", e);
        return buildBadRequest(e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Method argument type mismatch", e);
        return buildBadRequest(e);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException e) {
        log.warn("Bad request", e);
        return buildBadRequest(e);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        log.warn("Object not found", e);
        final ApiError error = ApiError.builder()
                .status("NOT_FOUND")
                .reason("The required object was not found")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException e) {
        log.warn("Conflict while processing request", e);
        final ApiError error = ApiError.builder()
                .status("CONFLICT")
                .reason("For the requested operation the conditions are not met")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Throwable.class)
    private static ResponseEntity<ApiError> handleThrowable(Throwable e) {
        final ApiError error = ApiError.builder()
                .status("INTERNAL_SERVER_ERROR")
                .reason("Unexpected error")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}