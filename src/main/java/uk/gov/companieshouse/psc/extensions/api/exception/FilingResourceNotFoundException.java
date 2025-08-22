package uk.gov.companieshouse.psc.extensions.api.exception;

/**
 * Exception thrown when a filing resource cannot be found.
 */
public class FilingResourceNotFoundException extends RuntimeException {
    
    public FilingResourceNotFoundException(String message) {
        super(message);
    }
    
    public FilingResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}