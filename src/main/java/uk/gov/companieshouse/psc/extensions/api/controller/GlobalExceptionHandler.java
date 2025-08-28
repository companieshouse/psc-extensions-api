package uk.gov.companieshouse.psc.extensions.api.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.psc.extensions.api.exception.InvalidFilingException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger("psc-extensions-api");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        logger.error("Validation failed", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Validation failed: " + e.getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getField() + " " + error.getDefaultMessage())
                        .findFirst()
                        .orElse("Invalid request"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        logger.error("Constraint validation failed", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Validation failed: " + e.getMessage());
    }

    @ExceptionHandler(InvalidFilingException.class)
    public ResponseEntity<String> handleInvalidFilingException(InvalidFilingException e) {
        logger.error("Invalid filing exception occurred", e);
        String errorMessage = e.getValidationErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid filing data");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Validation failed: " + errorMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        logger.error("Runtime exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        logger.error("Unexpected exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }
}