package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;

public interface PscExtensionDetailsService {
    
    /**
     * Retrieves the PSC individual extension request details from the Oracle query API.
     *
     * @return the PSC individual extension request details
     */
    void getPscExtensionDetails(final String pscNotificationId, final String ericPassThroughHeader);

}