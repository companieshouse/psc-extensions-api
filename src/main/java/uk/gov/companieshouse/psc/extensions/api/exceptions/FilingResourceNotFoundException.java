package uk.gov.companieshouse.psc.extensions.api.exceptions;

/**
 * PSC Verification resource not found.
 */
public class FilingResourceNotFoundException extends RuntimeException {

    public FilingResourceNotFoundException(final String message) {
        super(message);
    }

    public FilingResourceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
