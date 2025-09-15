package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import org.junit.jupiter.api.function.Executable;
import uk.gov.companieshouse.psc.extensions.api.sdk.companieshouse.ApiClientService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PscLookupServiceImplTest {

    private final String TRANSACTION_ID = "test-transaction-id";
    private final String COMPANY_NUMBER = "12345678";
    private final String PSC_NOTIFICATION_ID = "test-psc-notification-id";
    private final String API_KEY = "test-api-key";
    @Mock
    private ApiClientService apiClientService;
    @Mock
    private Logger logger;
    @Mock
    private EnvironmentReader environmentReader;
    @InjectMocks
    private PscLookupServiceImpl pscLookupService;
    private Transaction testTransaction;
    private PscExtensionsData testData;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(TRANSACTION_ID);

        testData = new PscExtensionsData();
        testData.setCompanyNumber(COMPANY_NUMBER);
        testData.setPscNotificationId(PSC_NOTIFICATION_ID);
    }

    @Test
    void constructor_ShouldSetDependencies() {
        PscLookupServiceImpl service = new PscLookupServiceImpl(apiClientService, logger, environmentReader);
        assertNotNull(service);
    }

    @Test
    void getPscIndividualFullRecord_WhenEnvironmentReaderFails_ShouldThrowRuntimeException() {
        when(environmentReader.getMandatoryString("CHS_INTERNAL_API_KEY"))
                .thenThrow(new RuntimeException("Environment error"));

        Executable action = () -> pscLookupService.getPscIndividualFullRecord(
                testTransaction.getId(),
                testData.getCompanyNumber(),
                testData.getPscNotificationId(),
                PscType.INDIVIDUAL
        );

        assertThrows(RuntimeException.class, action);
    }

    @Test
    void getPscIndividualFullRecord_ShouldCallEnvironmentReader() {
        when(environmentReader.getMandatoryString("CHS_INTERNAL_API_KEY")).thenReturn(API_KEY);
        when(apiClientService.getApiClient(anyString())).thenThrow(new RuntimeException("Expected test error"));

        Executable action = () -> pscLookupService.getPscIndividualFullRecord(
                testTransaction.getId(),
                testData.getCompanyNumber(),
                testData.getPscNotificationId(),
                PscType.INDIVIDUAL
        );

        assertThrows(RuntimeException.class, action);
    }

    @Test
    void serviceFields_ShouldNotBeNull() {
        assertNotNull(pscLookupService);
    }
}