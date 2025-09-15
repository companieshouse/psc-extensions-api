package uk.gov.companieshouse.psc.extensions.api.exception;

/**
 * Exception thrown when a problem is encountered with PSC extension requests.
 */
public class ExtensionRequestServiceException extends RuntimeException {

    public ExtensionRequestServiceException(String message) {
        super(message);
    }

    public ExtensionRequestServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}