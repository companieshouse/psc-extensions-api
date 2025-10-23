package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.psc.IndividualFullRecord;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;

public interface PscLookupService {
    
    /**
     * Retrieves the PSC individual full record from the PSC Data API.
     *
     * @param companyNumber the company number
     * @param pscAppointmentId the psc appointment id
     * @param pscType the type of PSC (individual/corporate)
     * @return the PSC individual full record
     * @throws PscLookupServiceException if PSC cannot be found or accessed
     */
    IndividualFullRecord getPscIndividualFullRecord(String companyNumber,
                                                          String pscAppointmentId,
                                                          PscType pscType) throws PscLookupServiceException;
}