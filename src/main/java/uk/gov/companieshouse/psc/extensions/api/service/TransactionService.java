package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.psc.extensions.api.exception.TransactionServiceException;

/**
 * Interacts with the external Transactions service to retrieve and update Transactions.
 */
public interface TransactionService {

    /**
     * Update a Transaction by ID.
     *
     * @param transaction the Transaction object to update
     *
     * @throws TransactionServiceException if Transaction not found or an error occurred
     */
    void updateTransaction(Transaction transaction)
            throws TransactionServiceException;
}