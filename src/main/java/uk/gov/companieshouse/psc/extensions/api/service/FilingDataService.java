package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface FilingDataService {
    
    /**
     * Generate a FilingApi object for the given filing ID and transaction.
     * This is called by the filing-resource-handler to get the filing type/subtype
     * which is then used by chips-filing-consumer.
     *
     * @param filingId the filing ID (extension resource ID)
     * @param transaction the transaction containing the filing
     * @return FilingApi with extension data formatted for CHIPS
     */
    FilingApi generateFilingApi(String filingId, Transaction transaction);
}