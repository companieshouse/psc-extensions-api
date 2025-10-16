package uk.gov.companieshouse.psc.extensions.api.service.impl;

import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.validator.ExtensionRequestDateValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PscExtensionsServiceImpl implements PscExtensionsService {

    private final PscExtensionsRepository repository;
    private static final Logger logger = LoggerFactory.getLogger("logger");

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
     * Query the mongoDB for the number psc extension requests.
     *
     * @param pscNotificationId the PSC ID
     * @return the number of psc extension requests if found.
     */
    @Override
    public Optional<Long> getExtensionCount(String pscNotificationId) {

        long count = repository.countByDataPscNotificationId(pscNotificationId);

        logger.info("Repository contains " + count + " extensions for ID: " + pscNotificationId);

        return count > 0 ? Optional.of(count) : Optional.empty();
    }

    /**
     * Validate whether an extension request is valid.
     *
     * @param idvDetails     identity verification details of PSC requesting extension
     * @param extensionCount number of PSC extension requests
     * @return an array of validation errors
     */
    @Override
    public ValidationStatusError[] validateExtensionRequest(IdentityVerificationDetails idvDetails, Optional<Long> extensionCount) {
        List<ValidationStatusError> errors = new ArrayList<>();

        //1. Date validation
        errors.addAll(ExtensionRequestDateValidator.validate(idvDetails));

        //2. Count validation should be <=2
        if (extensionCount.isPresent() && extensionCount.get() > 2) {
            String errorResponseText = "PSC Exceeded maximum number of extension requests";
            errors.add(new ValidationStatusError(errorResponseText, "$.", "json-path", "ch:validation"));
        }

        return errors.toArray(new ValidationStatusError[0]);
    }
}
