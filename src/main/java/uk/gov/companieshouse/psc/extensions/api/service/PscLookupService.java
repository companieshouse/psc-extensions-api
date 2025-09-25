package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;

public interface PscLookupService {
    
    /**
     * Retrieves the PSC individual full record from the PSC Data API.
     *
     * @param transactionId the id of the transaction
     * @param companyNumber the company number
     * @param pscAppointmentId the psc appointment id
     * @param pscType the type of PSC (individual/corporate)
     * @return the PSC individual full record
     * @throws PscLookupServiceException if PSC cannot be found or accessed
     */
    // TODO: in psc-verification-api, the 'pscAppointmentId' is named 'pscNotificationId' - why has this changed here?
    PscIndividualFullRecordApi getPscIndividualFullRecord(String transactionId,
                                                          String companyNumber,
                                                          String pscAppointmentId,
                                                         PscType pscType) throws PscLookupServiceException;
}