package uk.gov.companieshouse.psc.extensions.api.service.impl;

import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;

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
     * @param pscNotificationId   the PSC ID
     * @return the number of psc extension requests if found.
     *
     */

    @Override
    public Optional<Long> getExtensionCount(String pscNotificationId) {

        long count = repository.countByDataPscNotificationId(pscNotificationId);

        logger.info("Repository contains " + count + " extensions for ID: " + pscNotificationId);

        return count > 0 ? Optional.of(count) : Optional.empty();
    }
}