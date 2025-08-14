package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.pscverification.PscVerificationData;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exceptions.PscLookupServiceException;

/**
 * Interacts with the external CHS PSC API service to retrieve PSCs.
 */
public interface PscLookupService {

    PscIndividualFullRecordApi getPscIndividualFullRecord(Transaction transaction, PscVerificationData data, PscType pscType)
            throws PscLookupServiceException;
}
