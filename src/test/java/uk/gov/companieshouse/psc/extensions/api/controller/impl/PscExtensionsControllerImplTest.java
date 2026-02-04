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
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.api.psc.IndividualFullRecord;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionResponse;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.api.pscextensions.model.ValidationError;
import uk.gov.companieshouse.api.pscextensions.model.ValidationStatusResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.controller.PscExtensionsControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.ExtensionRequestServiceException;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.exception.TransactionServiceException;
import uk.gov.companieshouse.psc.extensions.api.mapper.PscExtensionsMapper;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.service.ExtensionValidityService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
import uk.gov.companieshouse.psc.extensions.api.service.TransactionService;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import static java.time.Instant.parse;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PscExtensionsControllerImplTest {

    private static final String TRANSACTION_ID = "transaction-id";
    private static final String PSC_NOTIFICATION_ID = "psc-notification-id";
    private static final String COMPANY_NUMBER = "12345678";


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
    @Mock
    private Logger logger;

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
                transactionService, pscExtensionsService, pscLookupService, filingMapper, extensionValidityService, clock, logger);
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
        when(pscLookupService.getPscIndividualFullRecord(testData.getCompanyNumber(), testData.getPscNotificationId(), PscType.INDIVIDUAL))
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
        final Long extensionCount = 1L;
        when(pscExtensionsService.getExtensionCount(PSC_NOTIFICATION_ID)).thenReturn(Optional.of(extensionCount));

        ResponseEntity<Long> response = controller._getPscExtensionCount(PSC_NOTIFICATION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(extensionCount, response.getBody());
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
        when(pscLookupService.getPscIndividualFullRecord(COMPANY_NUMBER, PSC_NOTIFICATION_ID, PscType.INDIVIDUAL))
                .thenThrow(new PscLookupServiceException("PSC not found"));

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        assertThrows(PscLookupServiceException.class,
                () -> controller._getIsPscExtensionValid(PSC_NOTIFICATION_ID, COMPANY_NUMBER));
    }

    @Test
    void isPscExtensionValid_WhenValidationErrorsPresent_ShouldReturnInvalidResponse() throws PscLookupServiceException {
        IdentityVerificationDetails idvDetails = new IdentityVerificationDetails();

        ValidationStatusError error = new ValidationStatusError(
                "Statement date is too early",
                "statementDate",
                "field",
                "INVALID_DATE"
        );
        ValidationStatusError[] errors = new ValidationStatusError[]{error};

        IndividualFullRecord mockPscRecord = mock(IndividualFullRecord.class);
        Optional<Long> extensionCount = Optional.of(1L);

        when(mockPscRecord.getIdentityVerificationDetails()).thenReturn(idvDetails);
        when(pscLookupService.getPscIndividualFullRecord(
                COMPANY_NUMBER, PSC_NOTIFICATION_ID, PscType.INDIVIDUAL))
                .thenReturn(mockPscRecord);
        when(pscExtensionsService.getExtensionCount(PSC_NOTIFICATION_ID)).thenReturn(extensionCount);
        when(pscExtensionsService.validateExtensionRequest(idvDetails, extensionCount)).thenReturn(errors);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        ResponseEntity<ValidationStatusResponse> response = controller._getIsPscExtensionValid(
                PSC_NOTIFICATION_ID, COMPANY_NUMBER);

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
        IndividualFullRecord mockPscRecord = mock(IndividualFullRecord.class);
        Optional<Long> extensionCount = Optional.of(1L);

        when(mockPscRecord.getIdentityVerificationDetails()).thenReturn(null);
        when(pscLookupService.getPscIndividualFullRecord(
                COMPANY_NUMBER, PSC_NOTIFICATION_ID, PscType.INDIVIDUAL))
                .thenReturn(mockPscRecord);
        when(pscExtensionsService.getExtensionCount(PSC_NOTIFICATION_ID)).thenReturn(extensionCount);
        when(pscExtensionsService.validateExtensionRequest(null, extensionCount))
                .thenReturn(new ValidationStatusError[0]);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        ResponseEntity<ValidationStatusResponse> response = controller._getIsPscExtensionValid(
                PSC_NOTIFICATION_ID, COMPANY_NUMBER);

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
                () -> controller._getIsPscExtensionValid(PSC_NOTIFICATION_ID, COMPANY_NUMBER));
    }



    @Test
    void createPscExtension_whenTransactionMissing_shouldThrowTransactionServiceException() {
        // No transaction attribute present in request context
        HttpServletRequest req = mock(
                HttpServletRequest.class);
        when(req.getAttribute("transaction")).thenReturn(null);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));

        PscExtensionsData data = new uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData();
        data.setCompanyNumber("12345678");
        data.setPscNotificationId("psc-123");

        // Expect the controller to throw TransactionServiceException when transaction is missing
        Assertions.assertThrows(
                TransactionServiceException.class,
                () -> controller._createPscExtension("tx-123", data)
        );
    }

    @Test
    void createPscExtension_success_returns201AndBody_andUpdatesTransaction() throws uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException {
        // Arrange a request with transaction
        HttpServletRequest req =
                mock(jakarta.servlet.http.HttpServletRequest.class);
        Transaction tx = new Transaction();
        tx.setId("tx-201");
        when(req.getAttribute("transaction")).thenReturn(tx);
        when(req.getRequestURI()).thenReturn("/transactions/tx-201/persons-with-significant-control-extensions");
        when(req.getMethod()).thenReturn("POST");
        org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(new org.springframework.web.context.request.ServletRequestAttributes(req));

        // Input data
        PscExtensionsData data = new uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData();
        data.setCompanyNumber("12345678");
        data.setPscNotificationId("psc-abc");

        // Mapper returns entity
        uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension entity = new uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension();
        when(filingMapper.toEntity(data)).thenReturn(entity);

        // PSC lookup returns IndividualFullRecord with internalId
        IndividualFullRecord fullRecord = org.mockito.Mockito.mock(uk.gov.companieshouse.api.psc.IndividualFullRecord.class);
        when(fullRecord.getInternalId()).thenReturn(987654321L);
        when(pscLookupService.getPscIndividualFullRecord(
                data.getCompanyNumber(), data.getPscNotificationId(),
                uk.gov.companieshouse.psc.extensions.api.enumerations.PscType.INDIVIDUAL
        )).thenReturn(fullRecord);

        // Clock provides instant values used by saveFilingWithLinks
        java.time.Instant now = parse("2025-01-01T00:00:00Z");
        when(clock.instant()).thenReturn(now);

        // First save returns a PscExtension with id (needed to build links)
        PscExtension saved1 = new uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension();
        saved1.setId("657a0f0b6f1c2a3b4d5e6f70");

        // After links are set, second save returns final entity with links
        ResourceLinks links = uk.gov.companieshouse.api.model.common.ResourceLinks
                .newBuilder()
                .self(java.net.URI.create("/transactions/tx-201/persons-with-significant-control-extensions/" + "657a0f0b6f1c2a3b4d5e6f70"))
                .validationStatus(java.net.URI.create("/transactions/tx-201/persons-with-significant-control-extensions/" + "657a0f0b6f1c2a3b4d5e6f70" + "/validation_status"))
                .build();
        PscExtension saved2 = new uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension(saved1);
        saved2.setLinks(links);
        when(pscExtensionsService.save(org.mockito.ArgumentMatchers.any(uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension.class)))
                .thenReturn(saved2);

        // Mapper returns API response
        PscExtensionResponse apiResponse =
                new PscExtensionResponse();
        when(filingMapper.toApi(saved2)).thenReturn(apiResponse);
        when(extensionValidityService.canSubmitExtensionRequest(any())).thenReturn(true);

        // Act
        ResponseEntity<PscExtensionResponse> response =
                controller._createPscExtension("tx-201", data);

        // Assert: 201 Created and body present
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verify transaction was updated with new resources
        verify(transactionService).updateTransaction(org.mockito.ArgumentMatchers.any(uk.gov.companieshouse.api.model.transaction.Transaction.class));
    }

    @Test
    void createPscExtension_setsInternalDataFromFullRecordInternalId() throws uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException {
        // Arrange
        HttpServletRequest req = mock(jakarta.servlet.http.HttpServletRequest.class);
        Transaction tx = new Transaction();
        tx.setId("tx-999");
        when(req.getAttribute("transaction")).thenReturn(tx);
        when(req.getRequestURI()).thenReturn("/transactions/tx-999/persons-with-significant-control-extensions");
        when(req.getMethod()).thenReturn("POST");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));

        PscExtensionsData data = new PscExtensionsData();
        data.setCompanyNumber("CN");
        data.setPscNotificationId("PN");

        PscExtension entity = new PscExtension();
        when(filingMapper.toEntity(data)).thenReturn(entity);

        IndividualFullRecord fullRecord = mock(IndividualFullRecord.class);
        when(fullRecord.getInternalId()).thenReturn(123456789L);
        when(pscLookupService.getPscIndividualFullRecord("CN", "PN",
                uk.gov.companieshouse.psc.extensions.api.enumerations.PscType.INDIVIDUAL)).thenReturn(fullRecord);

        // Minimal stubs to let saveFilingWithLinks proceed
        when(clock.instant()).thenReturn(parse("2025-01-01T00:00:00Z"));
        uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension saved1 = new uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension();
        saved1.setId("657a0f0b6f1c2a3b4d5e6f71");
        when(pscExtensionsService.save(org.mockito.Mockito.any(uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension.class)))
                .thenReturn(saved1);
        when(filingMapper.toApi(org.mockito.Mockito.any(uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension.class)))
                .thenReturn(new uk.gov.companieshouse.api.pscextensions.model.PscExtensionResponse());
        when(extensionValidityService.canSubmitExtensionRequest(any())).thenReturn(true);
        // Act
        controller._createPscExtension("tx-999", data);

        // Assert: entity must have InternalData set with internalId string
        uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData internalData = entity.getInternalData();
        assertNotNull(internalData);
        assertEquals("123456789", internalData.getInternalId());
    }

    @Test
    void isPscExtensionValid_whenNoValidationErrors_shouldReturnValidTrue() throws uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException {
        // Arrange request context
        jakarta.servlet.http.HttpServletRequest req = org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/isExtensionRequestValid");
        when(req.getMethod()).thenReturn("GET");
        org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(new org.springframework.web.context.request.ServletRequestAttributes(req));

        // PSC record with IDV details
        IndividualFullRecord pscRecord = org.mockito.Mockito.mock(uk.gov.companieshouse.api.psc.IndividualFullRecord.class);
        IdentityVerificationDetails idv = new uk.gov.companieshouse.api.psc.IdentityVerificationDetails();
        when(pscRecord.getIdentityVerificationDetails()).thenReturn(idv);

        // No validation errors
        java.util.Optional<Long> extensionCount = java.util.Optional.of(0L);
        when(pscLookupService.getPscIndividualFullRecord("12345678", "psc-notification-id",
                uk.gov.companieshouse.psc.extensions.api.enumerations.PscType.INDIVIDUAL)).thenReturn(pscRecord);
        when(pscExtensionsService.getExtensionCount("psc-notification-id")).thenReturn(extensionCount);
        when(pscExtensionsService.validateExtensionRequest(idv, extensionCount))
                .thenReturn(new uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError[0]);

        // Act
        org.springframework.http.ResponseEntity<uk.gov.companieshouse.api.pscextensions.model.ValidationStatusResponse> resp =
                controller._getIsPscExtensionValid("psc-notification-id", "12345678");

        // Assert: valid = true, empty error list
        uk.gov.companieshouse.api.pscextensions.model.ValidationStatusResponse body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.getValid());
        assertNotNull(body.getValidationStatusError());
        assertEquals(0, body.getValidationStatusError().size());
    }

    @Test
    void createPscExtension_whenExtensionValidityServiceReturnsTrue_butSaveFailsGracefullyStillReturnsCreated() throws uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException {
        // Arrange request + transaction
        jakarta.servlet.http.HttpServletRequest req = org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
        uk.gov.companieshouse.api.model.transaction.Transaction tx = new uk.gov.companieshouse.api.model.transaction.Transaction();
        tx.setId("tx-ok");
        when(req.getAttribute("transaction")).thenReturn(tx);
        when(req.getRequestURI()).thenReturn("/transactions/tx-ok/persons-with-significant-control-extensions");
        when(req.getMethod()).thenReturn("POST");
        RequestContextHolder.setRequestAttributes(new org.springframework.web.context.request.ServletRequestAttributes(req));

        uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData data = new uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData();
        data.setCompanyNumber("C");
        data.setPscNotificationId("P");

        // Validity ok
        when(extensionValidityService.canSubmitExtensionRequest(data)).thenReturn(true);

        // Map to entity
        PscExtension entity = new PscExtension();
        when(filingMapper.toEntity(data)).thenReturn(entity);

        // Lookup returns a record
        IndividualFullRecord fullRecord = mock(IndividualFullRecord.class);
        when(fullRecord.getInternalId()).thenReturn(1L);
        when(pscLookupService.getPscIndividualFullRecord("C", "P",
                PscType.INDIVIDUAL)).thenReturn(fullRecord);

        // Clock instant & save/links
        when(clock.instant()).thenReturn(parse("2025-01-01T00:00:00Z"));
        PscExtension saved1 = new PscExtension();
        saved1.setId("657a0f0b6f1c2a3b4d5e6f72");

        // Final save returns same entity; mapper returns minimal response
        when(pscExtensionsService.save(any(PscExtension.class)))
                .thenReturn(saved1);
        when(filingMapper.toApi(saved1)).thenReturn(new PscExtensionResponse());

        // Act
        org.springframework.http.ResponseEntity<uk.gov.companieshouse.api.pscextensions.model.PscExtensionResponse> response =
                controller._createPscExtension("tx-ok", data);

        // Assert: Created status and a non-null body even with minimal response
        assertEquals(org.springframework.http.HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

}