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
    private final String PSC_NOTIFICATION_ID = "psc-notification-id";
    private final String COMPANY_NUMBER = "12345678";

    @Mock
    private PscExtensionsControllerImpl pscExtensionsController;
    @Mock
    private PscLookupService pscLookupService;
    @Mock
    private HttpServletRequest httpServletRequest;

    private ValidationStatusControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new ValidationStatusControllerImpl(pscExtensionsController, pscLookupService);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));
    }

    @Test
    void _validate_WhenValidationErrorsPresent_ShouldReturnInvalidResponse() {
        final PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        when(pscLookupService.getPscIndividualFullRecord(
                COMPANY_NUMBER, PSC_NOTIFICATION_ID, PscType.INDIVIDUAL))
                .thenReturn(mockPscRecord);

        final ValidationError error = new ValidationError();
        final List<ValidationError> errors = List.of(error);

        final ValidationStatusResponse validationStatusResponse = new ValidationStatusResponse();
        validationStatusResponse.setValid(false);
        validationStatusResponse.setValidationStatusError(errors);

        when(pscExtensionsController.getValidationStatus(PSC_NOTIFICATION_ID, mockPscRecord))
                .thenReturn(validationStatusResponse);

        final ResponseEntity<ValidationStatusResponse> response = controller._validate(PSC_NOTIFICATION_ID, COMPANY_NUMBER);

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getValid());
        assertFalse(body.getValidationStatusError().isEmpty());
        assertEquals(body.getValidationStatusError(), List.of(error));
    }

    @Test
    void _validate_WhenValidationErrorsAbsent_ShouldReturnValidResponse() {
        final PscIndividualFullRecordApi mockPscRecord = mock(PscIndividualFullRecordApi.class);
        when(pscLookupService.getPscIndividualFullRecord(
                COMPANY_NUMBER, PSC_NOTIFICATION_ID, PscType.INDIVIDUAL))
                .thenReturn(mockPscRecord);

        final List<ValidationError> errors = Collections.emptyList();

        final ValidationStatusResponse validationStatusResponse = new ValidationStatusResponse();
        validationStatusResponse.setValid(true);
        validationStatusResponse.setValidationStatusError(errors);

        when(pscExtensionsController.getValidationStatus(PSC_NOTIFICATION_ID, mockPscRecord))
                .thenReturn(validationStatusResponse);

        final ResponseEntity<ValidationStatusResponse> response = controller._validate(PSC_NOTIFICATION_ID, COMPANY_NUMBER);

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getValid());
        assertTrue(body.getValidationStatusError().isEmpty());
    }
}
