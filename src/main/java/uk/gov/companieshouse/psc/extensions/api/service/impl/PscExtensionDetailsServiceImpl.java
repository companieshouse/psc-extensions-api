package uk.gov.companieshouse.psc.extensions.api.service.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;
import uk.gov.companieshouse.psc.extensions.api.service.ApiClientService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionDetailsService;

@Service
public class PscExtensionDetailsServiceImpl implements PscExtensionDetailsService {

    private static final String UNEXPECTED_STATUS_CODE = "Unexpected Status Code received";
    private final ApiClientService apiClientService;
    private static final Logger logger = LoggerFactory.getLogger("logger");

    private final PscExtensionsRepository repository;

    @Autowired
    public PscExtensionDetailsServiceImpl(
            final ApiClientService apiClientService,final PscExtensionsRepository repository
    ) {
        this.apiClientService = apiClientService;
      this.repository = repository;
    }

    /**
     * Query the mongoDB for the number psc extension requests.
     *
     * @param pscNotificationId         the PSC ID
     * @return the number of psc extension requests if found.
     *
     */
    @Override
    public Optional<PscExtension> getExtensionCount(String pscNotificationId) {
        if (pscNotificationId == null || pscNotificationId.isEmpty()) {
            logger.error("Provided notification ID is missing");
            throw new NullPointerException("Notification ID cannot be null or empty");
        }

        long count = repository.countByDataPscNotificationId(pscNotificationId);
        if (count > 1) {
            logger.error("Multiple extensions found for notification ID: " + pscNotificationId);
            throw new IllegalArgumentException();

        }

        logger.info("Repository contains " + count + " extensions for ID: " + pscNotificationId);

        return repository.findById(pscNotificationId);
    }


}