package uk.gov.companieshouse.psc.extensions.api.exception;

/**
 * Exception thrown when a problem is encountered retrieving psc extension request details.
 */
public class PscExtensionDetailsServiceException extends Exception {

    public PscExtensionDetailsServiceException(String message) {
        super(message);
    }

    public PscExtensionDetailsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}