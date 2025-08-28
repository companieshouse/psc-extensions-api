package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.service.ApiClientService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;

import java.text.MessageFormat;

@Service
public class PscLookupServiceImpl implements PscLookupService {
    private static final String UNEXPECTED_STATUS_CODE = "Unexpected Status Code received";

    private final ApiClientService apiClientService;
    private final Logger logger;
    private final EnvironmentReader environmentReader;

    public PscLookupServiceImpl(ApiClientService apiClientService, Logger logger, EnvironmentReader environmentReader) {
        this.apiClientService = apiClientService;
        this.logger = logger;
        this.environmentReader = environmentReader;
    }

    /**
     * Retrieve a PSC by PscExtensionsData.
     *
     * @param transaction           the Transaction
     * @param data                  the PSC extensions data
     * @param pscType               the PSC Type
     * @return the PSC Full Record details, if found
     * @throws PscLookupServiceException if the PSC was not found or an error occurred
     */
    @Override
    public PscIndividualFullRecordApi getPscIndividualFullRecord(final Transaction transaction,
                                                                 final PscExtensionsData data,
                                                                 final PscType pscType)
            throws PscLookupServiceException {

        final var logMap = LogMapHelper.createLogMap(transaction.getId());
        String pscNotificationId = data.getPscNotificationId();
        String chsInternalApiKey = environmentReader.getMandatoryString("CHS_INTERNAL_API_KEY");

        try {
            final var uri = "/company/"
                    + data.getCompanyNumber()
                    + "/persons-with-significant-control/"
                    + pscType.getValue()
                    + "/"
                    + pscNotificationId
                    + "/full_record";

            return apiClientService.getApiClient(chsInternalApiKey)
                .pscs()
                .getIndividualFullRecord(uri)
                .execute()
                .getData();


        } catch (final ApiErrorResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                logger.errorContext(transaction.getId(), UNEXPECTED_STATUS_CODE, e, logMap);
                throw new FilingResourceNotFoundException(
                        MessageFormat.format("PSC Details not found for {0}: {1} {2}", pscNotificationId,
                                e.getStatusCode(), e.getStatusMessage()), e);
            }
            throw new PscLookupServiceException(
                    MessageFormat.format("Error Retrieving PSC details for {0}: {1} {2}", pscNotificationId,
                            e.getStatusCode(), e.getStatusMessage()), e);

        } catch (URIValidationException e) {
            logger.errorContext(transaction.getId(), UNEXPECTED_STATUS_CODE, e, logMap);
            throw new PscLookupServiceException(
                    MessageFormat.format("Error Retrieving PSC details for {0}: {1}", pscNotificationId,
                            e.getMessage()), e);
        }
    }
}