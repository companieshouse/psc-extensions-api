package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.exception.PscExtensionDetailsServiceException;
import uk.gov.companieshouse.psc.extensions.api.service.ApiClientService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionDetailsService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;

import java.io.IOException;
import java.text.MessageFormat;

@Service
public class PscExtensionDetailsServiceImpl implements PscExtensionDetailsService {

    private static final String UNEXPECTED_STATUS_CODE = "Unexpected Status Code received";
    private final ApiClientService apiClientService;
    private final Logger logger;

    public PscExtensionDetailsServiceImpl(
            final ApiClientService apiClientService,
            final Logger logger
    ) {
        this.apiClientService = apiClientService;
        this.logger = logger;
    }

    /**
     * Query the oracle query api for a psc extension request details.
     *
     * @param pscNotificationId         the PSC ID
     * @param ericPassThroughHeader includes authorisation for the psc extension request query
     * @return the psc extension request details if found
     *
     * @throws PscExtensionDetailsServiceException if not found or an error occurred
     */
    @Override
    public void getPscExtensionDetails(final String pscNotificationId,
            final String ericPassThroughHeader) throws PscExtensionDetailsServiceException {
        final var logMap = LogMapHelper.createLogMap(pscNotificationId);
        try {
            final var uri = "/corporate-body-appointments/persons-of-significant-control/identity-verification-extension-details" + pscNotificationId;
            final var pscExtensionDetails =
                    apiClientService.getApiClient(ericPassThroughHeader)
                            .pscs()
                            .getIndividualFullRecord(uri)
                            .execute()
                            .getData();
            logger.debugContext(pscNotificationId, "Retrieved psc extension request details", logMap);
            return pscExtensionDetails;
        }
        catch (final ApiErrorResponseException e) {
            logger.errorContext(pscNotificationId, UNEXPECTED_STATUS_CODE, e, logMap);
            throw new PscExtensionDetailsServiceException(
                    MessageFormat.format("Error retrieving extension request details for {0}: {1} {2}",
                            pscNotificationId, e.getStatusCode(), e.getStatusMessage()), e);
        }
        catch (final URIValidationException | IOException e) {
            throw new PscExtensionDetailsServiceException("Error Retrieving extension request details " + pscNotificationId,
                    e);
        }
    }

}