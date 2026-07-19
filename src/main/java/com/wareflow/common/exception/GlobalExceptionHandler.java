package com.wareflow.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiError> handleApplicationException(
            ApplicationException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = resolveStatus(exception.getErrorCode());

        ApiError apiError = createApiError(
                status,
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, List<String>> validationErrors =
                new LinkedHashMap<>();

        for (FieldError fieldError :
                exception.getBindingResult().getFieldErrors()) {

            validationErrors
                    .computeIfAbsent(
                            fieldError.getField(),
                            ignored -> new ArrayList<>()
                    )
                    .add(fieldError.getDefaultMessage());
        }

        ApiError apiError = createApiError(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                "Request validation failed",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, List<String>> validationErrors =
                new LinkedHashMap<>();

        for (ConstraintViolation<?> violation :
                exception.getConstraintViolations()) {

            String property = violation.getPropertyPath().toString();

            validationErrors
                    .computeIfAbsent(
                            property,
                            ignored -> new ArrayList<>()
                    )
                    .add(violation.getMessage());
        }

        ApiError apiError = createApiError(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                "Request validation failed",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedRequest(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        ApiError apiError = createApiError(
                HttpStatus.BAD_REQUEST,
                ErrorCode.MALFORMED_REQUEST,
                "Request body is missing or contains invalid JSON",
                request.getRequestURI(),
                null
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Unexpected error while processing request '{}'",
                request.getRequestURI(),
                exception
        );

        ApiError apiError = createApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR,
                "An unexpected internal server error occurred",
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    private HttpStatus resolveStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case USER_NOT_FOUND,
                 ROLE_NOT_FOUND -> HttpStatus.NOT_FOUND;

            case USERNAME_ALREADY_EXISTS,
                 EMAIL_ALREADY_EXISTS -> HttpStatus.CONFLICT;

            case USER_MUST_HAVE_AT_LEAST_ONE_ROLE,
                 VALIDATION_FAILED,
                 MALFORMED_REQUEST -> HttpStatus.BAD_REQUEST;

            case INTERNAL_SERVER_ERROR ->
                    HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private ApiError createApiError(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            String path,
            Map<String, List<String>> validationErrors
    ) {
        return new ApiError(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                errorCode.name(),
                message,
                path,
                validationErrors
        );
    }
}