package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

import java.util.Optional;

public interface PscExtensionsService {
    
    /**
     * Store a PscExtension entity in persistence layer.
     *
     * @param filing the PscExtension entity to store
     * @return the stored entity
     */
    PscExtension save(PscExtension filing);

    /**
     * Retrieve a stored PscExtension entity by Filing ID.
     *
     * @param filingId the Filing ID
     * @return the stored entity if found
     */
    Optional<PscExtension> get(String filingId);

    /**
     * Retrieves the number of PSC extension requests from MongoDB
     *
     * @return the number of PSC extension requests
     */
    Optional<Long> getExtensionCount(String pscNotificationId);

    /**
     * Validate whether an extension request is valid.
     *
     * @param idvDetails     identity verification details of PSC requesting extension
     * @param extensionCount number of PSC extension requests
     * @return an array of validation errors
     */
    ValidationStatusError[] validateExtensionRequest(IdentityVerificationDetails idvDetails, Optional<Long> extensionCount);
}
