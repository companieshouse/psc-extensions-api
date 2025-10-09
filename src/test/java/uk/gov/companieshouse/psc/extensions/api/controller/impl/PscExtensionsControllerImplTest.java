package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.model.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.api.pscextensions.model.ValidationError;
import uk.gov.companieshouse.api.pscextensions.model.ValidationStatusResponse;
import uk.gov.companieshouse.psc.extensions.api.controller.PscExtensionsControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.ExtensionRequestServiceException;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.mapper.PscExtensionsMapper;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.service.ExtensionValidityService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
import uk.gov.companieshouse.psc.extensions.api.service.TransactionService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    private Clock clock;
    @InjectMocks
    private PscExtensionsControllerImpl controller;
    private Transaction testTransaction;
    private PscExtensionsData testData;
    private PscExtension testEntity;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setup() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        lenient().when(mockRequest.getRequestURI()).thenReturn("/isExtensionRequestValid");
        lenient().when(mockRequest.getMethod()).thenReturn("GET");
    }

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
    void shouldReturnPscExtensionRequestCount_WhenPresent() {
        when(pscExtensionsService.getExtensionCount(PSC_NOTIFICATION_ID)).thenReturn(Optional.of(EXTENSION_COUNT));

        ResponseEntity<Long> response = controller._getPscExtensionCount(PSC_NOTIFICATION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EXTENSION_COUNT, response.getBody());
    }

    @Test
    void shouldReturnPscExtensionRequestCount_WhenNoExtensionRequestExists() {
        when(pscExtensionsService.getExtensionCount(PSC_NOTIFICATION_ID)).thenReturn(Optional.empty());

        ResponseEntity<Long> response = controller._getPscExtensionCount(PSC_NOTIFICATION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, response.getBody());
    }

    @Test
    void isPscExtensionValid_WhenPscLookupFails_ShouldThrowException() throws PscLookupServiceException {
        when(pscLookupService.getPscIndividualFullRecord(TRANSACTION_ID, COMPANY_NUMBER, PSC_NOTIFICATION_ID, PscType.INDIVIDUAL))
                .thenThrow(new PscLookupServiceException("PSC not found"));

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        assertThrows(PscLookupServiceException.class,
                () -> controller._getIsPscExtensionValid(TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER));
    }

    @Test
    void isPscExtensionValid_WhenValidationErrorsPresent_ShouldReturnInvalidResponse() throws PscLookupServiceException {
        IdentityVerificationDetails idvDetails = new IdentityVerificationDetails(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1)
        );

        ValidationStatusError error = new ValidationStatusError(
                "Statement date is too early",
                "statementDate",
                "field",
                "INVALID_DATE"
        );
        ValidationStatusError[] errors = new ValidationStatusError[]{error};

        PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        Optional<Long> extensionCount = Optional.of(1L);

        when(mockPscRecord.getIdentityVerificationDetails()).thenReturn(idvDetails);
        when(pscLookupService.getPscIndividualFullRecord(
                eq(TRANSACTION_ID), eq(COMPANY_NUMBER), eq(PSC_NOTIFICATION_ID), eq(PscType.INDIVIDUAL)))
                .thenReturn(mockPscRecord);
        when(pscExtensionsService.getExtensionCount(eq(PSC_NOTIFICATION_ID))).thenReturn(extensionCount);
        when(pscExtensionsService.validateExtensionRequest(eq(idvDetails), eq(extensionCount))).thenReturn(errors);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        ResponseEntity<ValidationStatusResponse> response = controller._getIsPscExtensionValid(
                TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER);

        ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getValid());

        List<ValidationError> validationErrors = body.getValidationStatusError();
        assertNotNull(validationErrors);
        assertEquals(1, validationErrors.size());

        ValidationError mappedError = validationErrors.getFirst();
        assertEquals("statementDate", mappedError.getField());
        assertEquals("Statement date is too early", mappedError.getMessage());
    }


    @Test
    void isPscExtensionValid_WhenIdentityVerificationDetailsIsNull_ShouldHandleGracefully() throws PscLookupServiceException {
        PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        Optional<Long> extensionCount = Optional.of(1L);

        when(mockPscRecord.getIdentityVerificationDetails()).thenReturn(null);
        when(pscLookupService.getPscIndividualFullRecord(
                eq(TRANSACTION_ID), eq(COMPANY_NUMBER), eq(PSC_NOTIFICATION_ID), eq(PscType.INDIVIDUAL)))
                .thenReturn(mockPscRecord);
        when(pscExtensionsService.getExtensionCount(eq(PSC_NOTIFICATION_ID))).thenReturn(extensionCount);
        when(pscExtensionsService.validateExtensionRequest(eq(null), eq(extensionCount)))
                .thenReturn(new ValidationStatusError[0]);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        ResponseEntity<ValidationStatusResponse> response = controller._getIsPscExtensionValid(
                TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER);

        ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getValid());
        assertNotNull(body.getValidationStatusError());
        assertEquals(0, body.getValidationStatusError().size());
    }


    @Test
    void isPscExtensionValid_WhenRequestContextMissing_ShouldThrowException() {
        RequestContextHolder.resetRequestAttributes();

        assertThrows(IllegalStateException.class,
                () -> controller._getIsPscExtensionValid(TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER));
    }

}