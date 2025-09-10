package uk.gov.companieshouse.psc.extensions.api.service;

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
     * Retrieves the number of PSC extension requests from our MongoDB
     *
     * @return the number of PSC extension requests
     */
    Optional<PscExtension> getExtensionCount(String pscNotificationId);
}