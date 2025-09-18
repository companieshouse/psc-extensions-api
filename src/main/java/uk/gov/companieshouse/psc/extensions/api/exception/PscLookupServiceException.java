package uk.gov.companieshouse.psc.extensions.api.exception;

/**
 * Exception thrown when a problem is encountered looking up PSC data.
 */
public class PscLookupServiceException extends RuntimeException {
    
    public PscLookupServiceException(String message) {
        super(message);
    }
    
    public PscLookupServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}