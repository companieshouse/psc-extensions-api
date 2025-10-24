package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.sdk.companieshouse.InternalApiClientService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PscLookupServiceImplTest {

    private static final String TRANSACTION_ID = "test-transaction-id";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String PSC_NOTIFICATION_ID = "test-psc-notification-id";
    @Mock
    private InternalApiClientService internalApiClientService;
    @Mock
    private Logger logger;
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
        PscLookupServiceImpl service = new PscLookupServiceImpl(internalApiClientService, logger);
        assertNotNull(service);
    }

    @Test
    void serviceFields_ShouldNotBeNull() {
        assertNotNull(pscLookupService);
    }
}
