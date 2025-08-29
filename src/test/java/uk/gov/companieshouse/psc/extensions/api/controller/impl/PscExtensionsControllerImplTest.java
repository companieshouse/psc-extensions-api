package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.controller.PscExtensionsControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.mapper.PscExtensionsMapper;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.service.ExtensionValidityService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
import uk.gov.companieshouse.psc.extensions.api.service.TransactionService;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PscExtensionsControllerImplTest {

    private final String TEST_TRANSACTION_ID = "test-transaction-id";
    private final String TEST_PSC_NOTIFICATION_ID = "test-psc-notification-id";
    private final String TEST_COMPANY_NUMBER = "12345678";
    private final Instant TEST_TIME = Instant.parse("2024-01-15T10:00:00Z");
    @Mock
    private TransactionService transactionService;
    @Mock
    private PscExtensionsService pscExtensionsService;
    @Mock
    private PscLookupService pscLookupService;
    @Mock
    private PscExtensionsMapper filingMapper;
    @Mock
    private ExtensionValidityService extensionValidityService;
    @Mock
    private Clock clock;
    @InjectMocks
    private PscExtensionsControllerImpl controller;
    private Transaction testTransaction;
    private PscExtensionsData testData;
    private PscExtension testEntity;
    private PscIndividualFullRecordApi testPscRecord;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(TEST_TRANSACTION_ID);
        testTransaction.setCompanyNumber(TEST_COMPANY_NUMBER);

        testData = new PscExtensionsData();
        testData.setCompanyNumber(TEST_COMPANY_NUMBER);
        testData.setPscNotificationId(TEST_PSC_NOTIFICATION_ID);

        testEntity = new PscExtension();
        testEntity.setId("test-entity-id");

        InternalData internalData = new InternalData();
        internalData.setInternalId("appointment-123");
        testEntity.setInternalData(internalData);

        testPscRecord = new PscIndividualFullRecordApi();
        testPscRecord.setInternalId(123L);
        testPscRecord.setName("Test Person");

        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    void constructor_ShouldSetDependencies() {
        PscExtensionsControllerImpl testController = new PscExtensionsControllerImpl(
                transactionService, pscExtensionsService, pscLookupService, filingMapper, extensionValidityService, clock);
        assertNotNull(testController);
    }

    @Test
    void createPscExtension_WhenExtensionNotValid_ShouldThrowRuntimeException() {
        when(mockRequest.getAttribute("transaction")).thenReturn(testTransaction);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        when(extensionValidityService.canSubmitExtensionRequest(testData)).thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> controller._createPscExtension(TEST_TRANSACTION_ID, testData)
        );
    }

    @Test
    void createPscExtension_WhenPscLookupFails_ShouldThrowPscLookupServiceException() throws PscLookupServiceException {
        when(mockRequest.getRequestURI()).thenReturn("/test-uri");
        when(mockRequest.getAttribute("transaction")).thenReturn(testTransaction);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        when(extensionValidityService.canSubmitExtensionRequest(testData)).thenReturn(true);
        when(filingMapper.toEntity(testData)).thenReturn(testEntity);
        when(pscLookupService.getPscIndividualFullRecord(testTransaction.getId(), testData.getCompanyNumber(), testData.getPscNotificationId(), PscType.INDIVIDUAL))
                .thenThrow(new PscLookupServiceException("PSC not found"));

        assertThrows(
                PscLookupServiceException.class,
                () -> controller._createPscExtension(TEST_TRANSACTION_ID, testData)
        );
    }

    @Test
    void serviceFields_ShouldNotBeNull() {
        assertNotNull(controller);
    }
}