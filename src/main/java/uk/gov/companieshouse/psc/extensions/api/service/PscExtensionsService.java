package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtensions;

import java.util.Optional;

public interface PscExtensionsService {
    
    /**
     * Store a PscExtensions entity in persistence layer.
     *
     * @param filing the PscExtensions entity to store
     * @return the stored entity
     */
    PscExtensions save(PscExtensions filing);

    /**
     * Retrieve a stored PscExtensions entity by Filing ID.
     *
     * @param filingId the Filing ID
     * @return the stored entity if found
     */
    Optional<PscExtensions> get(String filingId);
}