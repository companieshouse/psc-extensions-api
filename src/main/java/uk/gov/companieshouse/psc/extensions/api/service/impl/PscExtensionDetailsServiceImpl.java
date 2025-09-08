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

//    /**
//     * Query the oracle query api for a psc extension request details.
//     *
//     * @param pscNotificationId         the PSC ID
//     * @param ericPassThroughHeader includes authorisation for the psc extension request query
//     * @return the psc extension request details if found
//     *
//     * @throws PscExtensionDetailsServiceException if not found or an error occurred
//     */
//    @Override
//    public void getPscExtensionDetails(final String pscNotificationId,
//            final String ericPassThroughHeader) throws PscExtensionDetailsServiceException {
//        final var logMap = LogMapHelper.createLogMap(pscNotificationId);
//        try {
//            final var uri = "/corporate-body-appointments/persons-of-significant-control/identity-verification-extension-details" + pscNotificationId;
//            final var pscExtensionDetails =
//                    apiClientService.getApiClient(ericPassThroughHeader)
//                            .pscs()
//                            .getIndividualFullRecord(uri)
//                            .execute()
//                            .getData();
//            logger.debugContext(pscNotificationId, "Retrieved psc extension request details", logMap);
//            return pscExtensionDetails;
//        }
//        catch (final ApiErrorResponseException e) {
//            logger.errorContext(pscNotificationId, UNEXPECTED_STATUS_CODE, e, logMap);
//            throw new PscExtensionDetailsServiceException(
//                    MessageFormat.format("Error retrieving extension request details for {0}: {1} {2}",
//                            pscNotificationId, e.getStatusCode(), e.getStatusMessage()), e);
//        }
//        catch (final URIValidationException | IOException e) {
//            throw new PscExtensionDetailsServiceException("Error Retrieving extension request details " + pscNotificationId,
//                    e);
//        }
//    }

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