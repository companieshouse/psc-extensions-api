package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtensions;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;

import java.util.Optional;

@Service
public class PscExtensionsServiceImpl implements PscExtensionsService {
    
    private final PscExtensionsRepository repository;

    @Autowired
    public PscExtensionsServiceImpl(PscExtensionsRepository repository) {
        this.repository = repository;
    }

    /**
     * Store a PscExtensions entity in persistence layer.
     *
     * @param filing the PscExtensions entity to store
     * @return the stored entity
     */
    @Override
    public PscExtensions save(PscExtensions filing) {
        return repository.save(filing);
    }

    /**
     * Retrieve a stored PscExtensions entity by Filing ID.
     *
     * @param filingId the Filing ID
     * @return the stored entity if found
     */
    @Override
    public Optional<PscExtensions> get(String filingId) {
        return repository.findById(filingId);
    }
}