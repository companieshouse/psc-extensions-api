package uk.gov.companieshouse.psc.extensions.api.service.impl;

import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.exception.TransactionServiceException;
import uk.gov.companieshouse.psc.extensions.api.sdk.companieshouse.InternalApiClientService;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    private static final String TRANSACTION_ID = "test-transaction-id";
    @Mock
    private InternalApiClientService internalApiClientService;
    @Mock
    private Logger logger;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        Transaction testTransaction = new Transaction();
        testTransaction.setId(TRANSACTION_ID);
        testTransaction.setCompanyNumber("12345678");
        testTransaction.setDescription("Test transaction");
    }

    @Test
    void constructor_ShouldSetDependencies() {
        TransactionServiceImpl service = new TransactionServiceImpl(internalApiClientService, logger);
        assertNotNull(service);
    }

    @Test
    void serviceFields_ShouldNotBeNull() {
        assertNotNull(transactionService);
    }


    @Test
    void constructor_AllowsNullInternalApiClientService() {
        TransactionServiceImpl service = new TransactionServiceImpl(null, logger);
        assertNotNull(service);
    }

    @Test
    void constructor_AllowsNullLogger() {
        TransactionServiceImpl service = new TransactionServiceImpl(internalApiClientService, null);
        assertNotNull(service);
    }

    @Test
    void updateTransaction_WithNullTransaction_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> transactionService.updateTransaction(null));
    }

    @Test
    void updateTransaction_WithValidTransaction_WithoutStubs_ThrowsNullPointerException() {
        Transaction tx = new Transaction();
        tx.setId("tx-123");
        tx.setCompanyNumber("12345678");
        tx.setDescription("Valid tx but no internal client stubs");


        assertThrows(NullPointerException.class, () -> transactionService.updateTransaction(tx));
    }


    @Test
    void updateTransaction_executeThrowsApiError_isCaughtAndWrapped() throws Exception {
        // Arrange
        Transaction tx = new Transaction();
        tx.setId("tx-api-error");

        InternalApiClient internalApiClient =
                mock(uk.gov.companieshouse.api.InternalApiClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);

        String uri = "/private/transactions/" + tx.getId();
        when(
                internalApiClient.privateTransaction()
                        .patch(uri, tx)
                        .execute()
        ).thenThrow(
                new ApiErrorResponseException(
                        new HttpResponseException.Builder(
                                500, "Internal Error", new com.google.api.client.http.HttpHeaders()
                        )
                )
        );

        when(internalApiClientService.getInternalApiClient())
                .thenReturn(internalApiClient);

        // Act + Assert: should be wrapped into TransactionServiceException
        assertThrows(
                TransactionServiceException.class,
                () -> transactionService.updateTransaction(tx)
        );
    }

    @Test
    void updateTransaction_executeThrowsUriValidation_isCaughtAndWrapped() throws Exception {
        // Arrange
        Transaction tx = new Transaction();
        tx.setId("tx-uri-error");

        InternalApiClient internalApiClient =
                mock(uk.gov.companieshouse.api.InternalApiClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);

        String uri = "/private/transactions/" + tx.getId();
        when(
                internalApiClient.privateTransaction()
                        .patch(uri, tx)
                        .execute()
        ).thenThrow(new uk.gov.companieshouse.api.handler.exception.URIValidationException("bad uri"));

        when(internalApiClientService.getInternalApiClient())
                .thenReturn(internalApiClient);

        // Act + Assert: should be wrapped into TransactionServiceException
        assertThrows(
                TransactionServiceException.class,
                () -> transactionService.updateTransaction(tx)
        );
    }

}