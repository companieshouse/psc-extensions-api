package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.sdk.companieshouse.InternalApiClientService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}