package uk.gov.companieshouse.psc.extensions.api.service;

import java.util.Optional;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

public interface PscExtensionDetailsService {

    /**
     * Retrieves the PSC individual extension request details from the Oracle query API.
     *
     * @return the PSC individual extension request details
     */
//    void getPscExtensionDetails(final String pscNotificationId, final String ericPassThroughHeader);
//

    /**
     * Retrieves the number of PSC extension requests from our MongoDB
     *
     * @return the number of PSC extension requests
     */
    Optional<PscExtension> getExtensionCount(String pscNotificationId);
}