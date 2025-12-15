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
import uk.gov.companieshouse.api.psc.IndividualFullRecord;
import uk.gov.companieshouse.api.pscextensions.model.ValidationError;
import uk.gov.companieshouse.api.pscextensions.model.ValidationStatusResponse;
import uk.gov.companieshouse.psc.extensions.api.controller.PscExtensionsControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.controller.ValidationStatusControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationStatusControllerImplTest {
    private final String transactionId = "transaction-id";
    private final String filingResourceId = "filing-resource-id";
    private final String pscNotificationId = "psc-notification-id";
    private final String companyNumber = "12345678";

    @Mock
    private PscExtensionsControllerImpl pscExtensionsController;
    @Mock
    private PscLookupService pscLookupService;
    @Mock
    private PscExtensionsService pscExtensionsService;
    @Mock
    private HttpServletRequest httpServletRequest;

    private ValidationStatusControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new ValidationStatusControllerImpl(pscExtensionsController, pscLookupService, pscExtensionsService);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));
    }

    @Test
    void _validate_WhenValidationErrorsPresent_ShouldReturnInvalidResponse() {
        final PscExtension pscExtension = mock(PscExtension.class);
        final Data data = mock(Data.class);
        when(pscExtension.getData()).thenReturn(data);
        when(data.getCompanyNumber()).thenReturn(companyNumber);
        when(data.getPscNotificationId()).thenReturn(pscNotificationId);

        when(pscExtensionsService.get(filingResourceId)).thenReturn(Optional.of(pscExtension));

        final IndividualFullRecord mockPscRecord = mock(IndividualFullRecord.class);
        when(pscLookupService.getPscIndividualFullRecord(
                companyNumber, pscNotificationId, PscType.INDIVIDUAL))
                .thenReturn(mockPscRecord);

        final ValidationError error = new ValidationError();
        final List<ValidationError> errors = List.of(error);

        final ValidationStatusResponse validationStatusResponse = new ValidationStatusResponse();
        validationStatusResponse.setValid(false);
        validationStatusResponse.setValidationStatusError(errors);

        when(pscExtensionsController.getValidationStatus(pscNotificationId, mockPscRecord))
                .thenReturn(validationStatusResponse);

        final ResponseEntity<ValidationStatusResponse> response = controller._validate(transactionId, filingResourceId);

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getValid());
        assertEquals(body.getValidationStatusError(), List.of(error));
    }

    @Test
    void _validate_WhenValidationErrorsAbsent_ShouldReturnValidResponse() {
        final PscExtension pscExtension = mock(PscExtension.class);
        final Data data = mock(Data.class);
        when(pscExtension.getData()).thenReturn(data);
        when(data.getCompanyNumber()).thenReturn(companyNumber);
        when(data.getPscNotificationId()).thenReturn(pscNotificationId);

        when(pscExtensionsService.get(filingResourceId)).thenReturn(Optional.of(pscExtension));

        final IndividualFullRecord mockPscRecord = mock(IndividualFullRecord.class);
        when(pscLookupService.getPscIndividualFullRecord(
                companyNumber, pscNotificationId, PscType.INDIVIDUAL))
                .thenReturn(mockPscRecord);

        final List<ValidationError> errors = Collections.emptyList();

        final ValidationStatusResponse validationStatusResponse = new ValidationStatusResponse();
        validationStatusResponse.setValid(true);
        validationStatusResponse.setValidationStatusError(errors);

        when(pscExtensionsController.getValidationStatus(pscNotificationId, mockPscRecord))
                .thenReturn(validationStatusResponse);

        final ResponseEntity<ValidationStatusResponse> response = controller._validate(transactionId, filingResourceId);

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getValid());
        assertTrue(body.getValidationStatusError().isEmpty());
    }

    @Test
    void _validate_WhenPscExtensionNotFound_ShouldReturnInvalidResponse() {
        final var expectedErrorMessage = String.format("PSC extension not found when validating filing for %s", filingResourceId);

        when(pscExtensionsService.get(filingResourceId)).thenReturn(Optional.empty());

        final ResponseEntity<ValidationStatusResponse> response = controller._validate(transactionId, filingResourceId);

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getValid());
        assertEquals(body.getValidationStatusError().getFirst().getMessage(), expectedErrorMessage);
    }

    @Test
    void _validate_WhenPscExtensionCompanyNameIsNull_ShouldReturnInvalidResponse() {
        final var expectedErrorMessage = String.format("Missing fields when validating filing for %s: %s",
                filingResourceId, "companyNumber");

        final PscExtension pscExtension = mock(PscExtension.class);
        final Data data = mock(Data.class);
        when(pscExtension.getData()).thenReturn(data);
        when(data.getCompanyNumber()).thenReturn(null);
        when(data.getPscNotificationId()).thenReturn(pscNotificationId);

        when(pscExtensionsService.get(filingResourceId)).thenReturn(Optional.of(pscExtension));

        final ResponseEntity<ValidationStatusResponse> response = controller._validate(transactionId, filingResourceId);

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getValid());
        assertEquals(body.getValidationStatusError().getFirst().getMessage(), expectedErrorMessage);
    }

    @Test
    void _validate_WhenPscExtensionPscNotificationIdIsNull_ShouldReturnInvalidResponse() {
        final var expectedErrorMessage = String.format("Missing fields when validating filing for %s: %s",
                filingResourceId, "pscNotificationId");

        final PscExtension pscExtension = mock(PscExtension.class);
        final Data data = mock(Data.class);
        when(pscExtension.getData()).thenReturn(data);
        when(data.getCompanyNumber()).thenReturn(companyNumber);
        when(data.getPscNotificationId()).thenReturn(null);

        when(pscExtensionsService.get(filingResourceId)).thenReturn(Optional.of(pscExtension));

        final ResponseEntity<ValidationStatusResponse> response = controller._validate(transactionId, filingResourceId);

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getValid());
        assertEquals(body.getValidationStatusError().getFirst().getMessage(), expectedErrorMessage);
    }

    @Test
    void _validate_WhenCompanyNumberAndPscExtensionPscNotificationIdIsNull_ShouldReturnInvalidResponse() {
        final var expectedErrorMessage = String.format("Missing fields when validating filing for %s: %s",
                filingResourceId, String.join(", ","companyNumber", "pscNotificationId"));

        final PscExtension pscExtension = mock(PscExtension.class);
        final Data data = mock(Data.class);
        when(pscExtension.getData()).thenReturn(data);
        when(data.getCompanyNumber()).thenReturn(null);
        when(data.getPscNotificationId()).thenReturn(null);

        when(pscExtensionsService.get(filingResourceId)).thenReturn(Optional.of(pscExtension));

        final ResponseEntity<ValidationStatusResponse> response = controller._validate(transactionId, filingResourceId);

        final ValidationStatusResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getValid());
        assertEquals(body.getValidationStatusError().getFirst().getMessage(), expectedErrorMessage);
    }
}
