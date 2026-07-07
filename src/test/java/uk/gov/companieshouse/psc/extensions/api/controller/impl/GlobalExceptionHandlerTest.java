package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.controller.GlobalExceptionHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private Logger logger;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler(logger);
    }

    // MethodArgumentNotValid Exception tests
    @Test
    void handleValidationException_returnsFirstFieldError() {
        FieldError fieldError = new FieldError("object", "email", "must not be blank");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException methodArgumentNotValidException = mock(MethodArgumentNotValidException.class);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<String> responseEntity = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);


        //Assertions
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Validation failed: email must not be blank", responseEntity.getBody());
        verify(logger).error("Validation failed", methodArgumentNotValidException);

    }

    @Test
    void handleValidationException_withMultipleErrors_returnsFirstOnly() {
        FieldError error1 = new FieldError("object", "name", "must not be null");
        FieldError error2 = new FieldError("object", "age", "must be positive");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException methodArgumentNotValidException = mock(MethodArgumentNotValidException.class);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<String> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        //Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed: name must not be null", response.getBody());
    }

    @Test
    void handleValidationException_withNoFieldErrors_returnsFallbackMessage() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        MethodArgumentNotValidException methodArgumentNotValidException = mock(MethodArgumentNotValidException.class);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<String> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        //Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed: Invalid request", response.getBody());
    }


    // ConstraintViolationException tests
    @Test
    void handleConstraintViolationException_returnsBadRequest() {
        ConstraintViolationException constraintViolationException = new ConstraintViolationException("id: must be positive", null);
        ResponseEntity<String> response = globalExceptionHandler.handleConstraintViolationException(constraintViolationException);

        //Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed: id: must be positive", response.getBody());
        verify(logger).error("Constraint validation failed", constraintViolationException);

    }

    // RunTimeException Tests
    @Test
    void handleRuntimeException_returnsInternalServerError() {
        RuntimeException runtimeException = new RuntimeException("something broke");
        ResponseEntity<String> response = globalExceptionHandler.handleRuntimeException(runtimeException);

        //Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
        verify(logger).error("Runtime exception occurred", runtimeException);
    }

    @Test
    void handleRuntimeException_withNullPointer_returnsInternalServerError() {
        NullPointerException nullPointerException = new NullPointerException("null ref");
        ResponseEntity<String> response = globalExceptionHandler.handleRuntimeException(nullPointerException);

        //Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }

    // GenericException Tests
    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception genericException = new Exception("unexpected failure");
        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(genericException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        //Assertions
        assertEquals("Internal server error", response.getBody());
        verify(logger).error("Unexpected exception occurred", genericException);
    }

    @Test
    void handleGenericException_withCheckedException_returnsInternalServerError() {
        Exception ioException = new java.io.IOException("disk full");
        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(ioException);

        //Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }

}

