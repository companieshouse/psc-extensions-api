package uk.gov.companieshouse.psc.extensions.api.exception;

/**
 * Exception thrown when a problem is encountered handling Transactions.
 */
public class TransactionServiceException extends RuntimeException {
    
    public TransactionServiceException(String message) {
        super(message);
    }
    
    public TransactionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}