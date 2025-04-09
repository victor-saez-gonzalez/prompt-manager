package com.vs.prompt.manager.web.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Environment environment;

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        return buildProblem(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1  // in case of duplicate keys, keep the first one
                ));

        log.warn("Validation failed: {}", errors);

        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST, "Validation Error", "Validation failed", request);
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {

        var errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(), // e.g. "createCategory.arg0.name"
                        ConstraintViolation::getMessage,
                        (msg1, msg2) -> msg1
                ));



        log.warn("Constraint violation: {}", errors);

        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST, "Constraint Violation", "Validation failed", request);
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, WebRequest request) {
        log.error("Unhandled exception", ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("Malformed JSON or invalid enum value: {}", ex.getMessage());

        ProblemDetail problem = buildProblem(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON or invalid value",
                "Request body contains invalid data. Please check the format and allowed values.",
                request
        );
        if(isDevProfileActive()) {
            problem.setProperty("rawError", ex.getMessage()); // useful for debugging
        }

        return ResponseEntity.badRequest().body(problem);
    }

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return problem;
    }

    private boolean isDevProfileActive() {
        return environment.matchesProfiles("dev");
    }


}