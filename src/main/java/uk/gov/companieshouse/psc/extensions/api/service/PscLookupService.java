package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;

public interface PscLookupService {
    
    /**
     * Retrieves the PSC individual full record from the PSC Data API.
     *
     * @param transaction the transaction containing company information
     * @param data the extension data containing PSC notification ID
     * @param pscType the type of PSC (individual/corporate)
     * @return the PSC individual full record
     * @throws PscLookupServiceException if PSC cannot be found or accessed
     */
    PscIndividualFullRecordApi getPscIndividualFullRecord(Transaction transaction, 
                                                         PscExtensionsData data, 
                                                         PscType pscType) throws PscLookupServiceException;
}