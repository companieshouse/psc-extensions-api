package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.model.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PscExtensionsControllerImplTest {

    private static final String TRANSACTION_ID = "test-transaction-id";
    private static final String PSC_NOTIFICATION_ID = "test-psc-notification-id";
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
    void isValid_WhenExtensionIsValid_ShouldReturnValidResponse() throws PscLookupServiceException {
        IdentityVerificationDetails idvDetails = new IdentityVerificationDetails(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1)
        );

        ValidationStatusError[] validationErrors = new ValidationStatusError[0];

        PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        when(mockPscRecord.getIdentityVerificationDetails()).thenReturn(idvDetails);

        when(pscLookupService.getPscIndividualFullRecord(
                eq(TRANSACTION_ID),
                eq(COMPANY_NUMBER),
                eq(PSC_NOTIFICATION_ID),
                eq(PscType.INDIVIDUAL)
        )).thenReturn(mockPscRecord);

        when(pscExtensionsService.validateExtensionRequest(any(IdentityVerificationDetails.class)))
                .thenReturn(validationErrors);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        ValidationStatusResponse response = controller.isValid(TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER);

        Assertions.assertTrue(response.isValid());
        Assertions.assertEquals(0, response.getValidationStatusError().length);
    }

    @Test
    void isValid_WhenPscLookupFails_ShouldThrowException() throws PscLookupServiceException {
        when(pscLookupService.getPscIndividualFullRecord(TRANSACTION_ID, COMPANY_NUMBER, PSC_NOTIFICATION_ID, PscType.INDIVIDUAL))
                .thenThrow(new PscLookupServiceException("PSC not found"));

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        assertThrows(PscLookupServiceException.class,
                () -> controller.isValid(TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER));
    }

    @Test
    void isValid_WhenValidationErrorsPresent_ShouldReturnInvalidResponse() throws PscLookupServiceException {
        IdentityVerificationDetails idvDetails = new IdentityVerificationDetails(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1)
        );

        ValidationStatusError error = new ValidationStatusError("INVALID_DATE", "Statement date is too early", "", "");
        ValidationStatusError[] errors = new ValidationStatusError[]{error};

        PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        when(mockPscRecord.getIdentityVerificationDetails()).thenReturn(idvDetails);
        when(pscLookupService.getPscIndividualFullRecord(
                eq(TRANSACTION_ID), eq(COMPANY_NUMBER), eq(PSC_NOTIFICATION_ID), eq(PscType.INDIVIDUAL)))
                .thenReturn(mockPscRecord);
        when(pscExtensionsService.validateExtensionRequest(any())).thenReturn(errors);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        ValidationStatusResponse response = controller.isValid(TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER);

        Assertions.assertFalse(response.isValid());
        Assertions.assertEquals(1, response.getValidationStatusError().length);
        Assertions.assertEquals("INVALID_DATE", response.getValidationStatusError()[0].getError());
    }

    @Test
    void isValid_WhenIdentityVerificationDetailsIsNull_ShouldHandleGracefully() throws PscLookupServiceException {
        PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        when(mockPscRecord.getIdentityVerificationDetails()).thenReturn(null);
        when(pscLookupService.getPscIndividualFullRecord(
                eq(TRANSACTION_ID), eq(COMPANY_NUMBER), eq(PSC_NOTIFICATION_ID), eq(PscType.INDIVIDUAL)))
                .thenReturn(mockPscRecord);
        when(pscExtensionsService.validateExtensionRequest(null)).thenReturn(new ValidationStatusError[0]);

        ValidationStatusResponse response = controller.isValid(TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER);

        Assertions.assertTrue(response.isValid());
        Assertions.assertEquals(0, response.getValidationStatusError().length);
    }

    @Test
    void isValid_WhenRequestContextMissing_ShouldThrowException() {
        RequestContextHolder.resetRequestAttributes();

        assertThrows(IllegalStateException.class,
                () -> controller.isValid(TRANSACTION_ID, PSC_NOTIFICATION_ID, COMPANY_NUMBER));
    }

}