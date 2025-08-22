package uk.gov.companieshouse.psc.extensions.api.exception;

import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Exception thrown when filing validation fails.
 */
public class InvalidFilingException extends RuntimeException {
    
    private final List<FieldError> validationErrors;
    
    public InvalidFilingException(List<FieldError> validationErrors) {
        super("Invalid filing data");
        this.validationErrors = validationErrors;
    }
    
    public List<FieldError> getValidationErrors() {
        return validationErrors;
    }
}