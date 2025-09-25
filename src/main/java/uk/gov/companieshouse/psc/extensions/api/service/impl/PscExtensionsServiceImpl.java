package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.validator.ExtensionRequestDateValidator;

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

    /**
     * Validate whether an extension request is valid.
     *
     * @param idvDetails identity verification details of PSC requesting extension
     * @return an array of validation errors
     */
    @Override
    public ValidationStatusError[] validateExtensionRequest(IdentityVerificationDetails idvDetails) {
        return ExtensionRequestDateValidator.validate(idvDetails)
                .toArray(new ValidationStatusError[0]);
    }
}