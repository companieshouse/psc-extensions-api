package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
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
     * Store a PscExtension entity in persistence layer.
     *
     * @param filing the PscExtension entity to store
     * @return the stored entity
     */
    @Override
    public PscExtension save(PscExtension filing) {
        return repository.save(filing);
    }

    /**
     * Retrieve a stored PscExtension entity by Filing ID.
     *
     * @param filingId the Filing ID
     * @return the stored entity if found
     */
    @Override
    public Optional<PscExtension> get(String filingId) {
        return repository.findById(filingId);
    }
}