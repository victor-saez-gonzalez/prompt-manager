package com.vs.prompt.manager.web.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import jakarta.validation.Path;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerStandaloneTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void shouldHandleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Not found");

        ProblemDetail result = handler.handleNoSuchElementException(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Resource Not Found");
        assertThat(result.getDetail()).isEqualTo("Not found");
    }

    @Test
    void shouldHandleValidationError() {
        var target = new Object();
        var bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "name", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleValidation(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getTitle()).isEqualTo("Validation Error");
        assertThat(response.getBody().getProperties()).containsKey("errors");
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(path.toString()).thenReturn("field.name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        var response = handler.handleConstraintViolation(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getTitle()).isEqualTo("Constraint Violation");
        assertThat(response.getBody().getProperties()).containsKey("errors");
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");

        ProblemDetail result = handler.handleGeneric(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("Unexpected error");
    }
}
