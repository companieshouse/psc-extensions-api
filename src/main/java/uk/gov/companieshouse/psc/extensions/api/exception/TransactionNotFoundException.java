package uk.gov.companieshouse.psc.extensions.api.exception;

/**
 * Exception thrown when a problem is encountered handling Transactions.
 */
public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
