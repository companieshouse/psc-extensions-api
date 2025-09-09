package uk.gov.companieshouse.psc.extensions.api.service;

import java.util.Optional;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

public interface PscExtensionDetailsService {

    /**
     * Retrieves the number of PSC extension requests from our MongoDB
     *
     * @return the number of PSC extension requests
     */
    Optional<PscExtension> getExtensionCount(String pscNotificationId);
}