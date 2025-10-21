package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.pscextensions.model.ValidationError;
import uk.gov.companieshouse.api.pscextensions.model.ValidationStatusResponse;
import uk.gov.companieshouse.psc.extensions.api.controller.PscExtensionsControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.controller.ValidationStatusControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationStatusControllerImplTest {
    private final String TRANSACTION_ID = "transaction-id";
    private final String PSC_NOTIFICATION_ID = "psc-notification-id";
    private final String COMPANY_NUMBER = "12345678";

    @Mock
    private PscExtensionsControllerImpl pscExtensionsController;
    @Mock
    private PscLookupService pscLookupService;

    private ValidationStatusControllerImpl controller;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        controller = new ValidationStatusControllerImpl(pscExtensionsController, pscLookupService);

        mockRequest = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    void constructor_ShouldSetDependencies() {
        ValidationStatusControllerImpl testController = new ValidationStatusControllerImpl(
                pscExtensionsController, pscLookupService);
        assertNotNull(testController);
    }

    @Test
    void _validate_WhenValidationErrorsPresent_ShouldReturnInvalidResponse() {
        final PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        // TODO - companyNumber and pscNotificationId
        when(pscLookupService.getPscIndividualFullRecord(
                TRANSACTION_ID, "", "", PscType.INDIVIDUAL))
                .thenReturn(mockPscRecord);

        final ValidationError error = new ValidationError();
        final List<ValidationError> errors = List.of(error);

        final ValidationStatusResponse validationStatusResponse = new ValidationStatusResponse();
        validationStatusResponse.setValid(false);
        validationStatusResponse.setValidationStatusError(errors);

        // TODO - pscNotificationId
        when(pscExtensionsController.getValidationStatus("", mockPscRecord))
                .thenReturn(validationStatusResponse);

        // TODO
        final ResponseEntity<ValidationStatusResponse> response = controller._validate(TRANSACTION_ID, "");

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getValid());
        assertFalse(body.getValidationStatusError().isEmpty());
    }

    @Test
    void _validate_WhenValidationErrorsAbsent_ShouldReturnValidResponse() {
        final PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        // TODO - companyNumber and pscNotificationId
        when(pscLookupService.getPscIndividualFullRecord(
                TRANSACTION_ID, "", "", PscType.INDIVIDUAL))
                .thenReturn(mockPscRecord);

        final List<ValidationError> errors = Collections.emptyList();

        final ValidationStatusResponse validationStatusResponse = new ValidationStatusResponse();
        validationStatusResponse.setValid(true);
        validationStatusResponse.setValidationStatusError(errors);

        // TODO - pscNotificationId
        when(pscExtensionsController.getValidationStatus("", mockPscRecord))
                .thenReturn(validationStatusResponse);

        // TODO
        final ResponseEntity<ValidationStatusResponse> response = controller._validate(TRANSACTION_ID, "");

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getValid());
        assertTrue(body.getValidationStatusError().isEmpty());
    }
}
