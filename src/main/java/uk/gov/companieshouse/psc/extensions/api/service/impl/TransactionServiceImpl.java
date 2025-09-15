package uk.gov.companieshouse.psc.extensions.api.service.impl;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.exception.TransactionServiceException;
import uk.gov.companieshouse.psc.extensions.api.sdk.companieshouse.ApiClientService;
import uk.gov.companieshouse.psc.extensions.api.sdk.companieshouse.InternalApiClientService;
import uk.gov.companieshouse.psc.extensions.api.service.TransactionService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;

import java.text.MessageFormat;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final String UNEXPECTED_STATUS_CODE = "Unexpected Status Code received";
    private final ApiClientService apiClientService;
    private final InternalApiClientService internalApiClientService;
    private final Logger logger;

    public TransactionServiceImpl(
            final ApiClientService apiClientService,
            final InternalApiClientService internalApiClientService,
            final Logger logger
    ) {
        this.apiClientService = apiClientService;
        this.internalApiClientService = internalApiClientService;
        this.logger = logger;

    }

    /**
     * Update a given transaction via the transaction service.
     *
     * @param transaction           the Transaction ID
     * @throws TransactionServiceException if the transaction update failed
     */
    @Override
    public void updateTransaction(final Transaction transaction)
            throws TransactionServiceException {
        final var logMap = LogMapHelper.createLogMap(transaction.getId());
        try {
            logger.debugContext(transaction.getId(), "Updating transaction", logMap);
            final var uri = "/private/transactions/" + transaction.getId();
            final var resp =
                    internalApiClientService.getInternalApiClient()
                            .privateTransaction()
                            .patch(uri, transaction)
                            .execute();

            if (HttpStatus.NO_CONTENT.value() != resp.getStatusCode()) {
                throw new ApiErrorResponseException(
                        new HttpResponseException.Builder(resp.getStatusCode(),
                                UNEXPECTED_STATUS_CODE, new HttpHeaders()));
            }
        }
        catch (final ApiErrorResponseException e) {
            logger.errorContext(transaction.getId(), UNEXPECTED_STATUS_CODE, e, logMap);
            throw new TransactionServiceException(
                    MessageFormat.format("Error Updating Transaction details for {0}: {1} {2}",
                            transaction.getId(), e.getStatusCode(), e.getStatusMessage()), e);

        }
        catch (final URIValidationException e) {
            logger.errorContext(transaction.getId(), UNEXPECTED_STATUS_CODE, e, logMap);
            throw new TransactionServiceException(
                    MessageFormat.format("Error Updating Transaction {0}: {1}", transaction.getId(),
                            e.getMessage()), e);
        }
    }

}