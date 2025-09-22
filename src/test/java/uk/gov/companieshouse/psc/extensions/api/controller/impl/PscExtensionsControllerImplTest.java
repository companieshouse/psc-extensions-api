package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.controller.PscExtensionsControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.ExtensionRequestServiceException;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.mapper.PscExtensionsMapper;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;
import uk.gov.companieshouse.psc.extensions.api.service.ExtensionValidityService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
import uk.gov.companieshouse.psc.extensions.api.service.TransactionService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PscExtensionsControllerImplTest {

    private final String TRANSACTION_ID = "transaction-id";
    private final String PSC_NOTIFICATION_ID = "psc-notification-id";
    private final String COMPANY_NUMBER = "12345678";
    private final Long EXTENSION_COUNT = 1L;

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
    private PscExtensionsRepository pscExtensionsRepository;
    @Mock
    private Clock clock;
    @InjectMocks
    private PscExtensionsControllerImpl controller;
    private Transaction testTransaction;
    private PscExtensionsData testData;
    private PscExtension testEntity;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(TRANSACTION_ID);
        testTransaction.setCompanyNumber(COMPANY_NUMBER);

        testData = new PscExtensionsData();
        testData.setCompanyNumber(COMPANY_NUMBER);
        testData.setPscNotificationId(PSC_NOTIFICATION_ID);

        testEntity = new PscExtension();
        testEntity.setId("test-entity-id");

        InternalData internalData = new InternalData();
        internalData.setInternalId("appointment-123");
        testEntity.setInternalData(internalData);

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
                ExtensionRequestServiceException.class,
                () -> controller._createPscExtension(TRANSACTION_ID, testData)
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
                () -> controller._createPscExtension(TRANSACTION_ID, testData)
        );
    }

    @Test
    void serviceFields_ShouldNotBeNull() {
        assertNotNull(controller);
    }

    @Test
    void shouldReturnPscExtensionRequestCount_WhenPresent(){
        when(pscExtensionsService.getExtensionCount(PSC_NOTIFICATION_ID)).thenReturn(Optional.of(EXTENSION_COUNT));

        ResponseEntity<Long> response = controller.getPscExtensionCount(PSC_NOTIFICATION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EXTENSION_COUNT, response.getBody());
    }
    @Test
    void shouldReturnPscExtensionRequestCount_WhenNoExtensionRequestExists(){
        when(pscExtensionsService.getExtensionCount(PSC_NOTIFICATION_ID)).thenReturn(Optional.empty());

        ResponseEntity<Long> response = controller.getPscExtensionCount(PSC_NOTIFICATION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, response.getBody());
    }

    @Test
    public void testValidDueDate() {
        String pscNotificationId = "123";
        LocalDate validDate = LocalDate.now().minusDays(10);

        when(pscExtensionsRepository.findDueDateByPscNotificationId(PSC_NOTIFICATION_ID)).thenReturn(Optional.of(validDate));

        Optional<LocalDate> result = pscExtensionsService.getExtensionDueDate(PSC_NOTIFICATION_ID);
        Assertions.assertEquals(validDate, result);
    }

    @Test
    public void testExpiredDueDateThrowsException() {
        String pscNotificationId = "456";
        LocalDate expiredDate = LocalDate.now().minusWeeks(3);

        when(pscExtensionsRepository.findDueDateByPscNotificationId(PSC_NOTIFICATION_ID)).thenReturn(Optional.of(expiredDate));

        Assertions.assertThrows(RuntimeException.class, () -> {
            pscExtensionsService.getExtensionDueDate(PSC_NOTIFICATION_ID);
        });
    }

    @Test
    public void testMissingDueDateThrowsException() {
        String pscNotificationId = "789";

        when(pscExtensionsRepository.findDueDateByPscNotificationId(PSC_NOTIFICATION_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            pscExtensionsService.getExtensionDueDate(PSC_NOTIFICATION_ID);
        });
    }
}