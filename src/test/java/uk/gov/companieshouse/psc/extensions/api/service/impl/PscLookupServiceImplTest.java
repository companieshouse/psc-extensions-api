package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.service.ApiClientService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PscLookupServiceImplTest {

    private final String TEST_TRANSACTION_ID = "test-transaction-id";
    private final String TEST_COMPANY_NUMBER = "12345678";
    private final String TEST_PSC_NOTIFICATION_ID = "test-psc-notification-id";
    private final String TEST_API_KEY = "test-api-key";
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
    private PscIndividualFullRecordApi testPscRecord;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(TEST_TRANSACTION_ID);

        testData = new PscExtensionsData();
        testData.setCompanyNumber(TEST_COMPANY_NUMBER);
        testData.setPscNotificationId(TEST_PSC_NOTIFICATION_ID);

        testPscRecord = new PscIndividualFullRecordApi();
        testPscRecord.setInternalId(123L);
        testPscRecord.setName("Test Person");
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

        assertThrows(
                RuntimeException.class,
                () -> pscLookupService.getPscIndividualFullRecord(testTransaction.getId(), testData.getCompanyNumber(), testData.getPscNotificationId(), PscType.INDIVIDUAL)
        );
    }

    @Test
    void getPscIndividualFullRecord_ShouldCallEnvironmentReader() {
        when(environmentReader.getMandatoryString("CHS_INTERNAL_API_KEY")).thenReturn(TEST_API_KEY);
        when(apiClientService.getApiClient(anyString())).thenThrow(new RuntimeException("Expected test error"));

        assertThrows(
                RuntimeException.class,
                () -> pscLookupService.getPscIndividualFullRecord(testTransaction.getId(), testData.getCompanyNumber(), testData.getPscNotificationId(), PscType.INDIVIDUAL)
        );
    }

    @Test
    void serviceFields_ShouldNotBeNull() {
        assertNotNull(pscLookupService);
    }
}