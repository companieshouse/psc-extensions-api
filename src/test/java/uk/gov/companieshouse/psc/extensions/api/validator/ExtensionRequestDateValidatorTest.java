//package uk.gov.companieshouse.psc.extensions.api.validator;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.validation.FieldError;
//import uk.gov.companieshouse.api.model.psc.IdentityVerificationDetails;
//import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
//import uk.gov.companieshouse.api.model.pscverification.PscVerificationData;
//import uk.gov.companieshouse.api.model.transaction.Transaction;
//import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
//import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
//import uk.gov.companieshouse.logging.Logger;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ExtensionRequestDateValidatorTest {
//
//    @Mock
//    private PscLookupService pscLookupService;
//    @Mock
//    private PscVerificationData pscVerificationData;
//    @Mock
//    private Transaction transaction;
//    @Mock
//    private PscIndividualFullRecordApi pscIndividualFullRecordApi;
//    @Mock
//    private Logger logger;
//    @Mock
//    private Map<String, String> validation;
//
//    private ExtensionRequestDateValidator validator;
//    private Set<FieldError> errors;
//    private PscType pscType;
//    private String passthroughHeader;
//
//    @BeforeEach
//    void setUp() {
//        validator = new ExtensionRequestDateValidator(pscLookupService, logger);
//        validator.validation = validation;
//
//        errors = new HashSet<>();
//        pscType = PscType.INDIVIDUAL;
//        passthroughHeader = "passthroughHeader";
//
//        when(transaction.getId()).thenReturn("123");
//        when(pscVerificationData.companyNumber()).thenReturn("12345678");
//        when(pscVerificationData.pscNotificationId()).thenReturn("456");
//        when(pscLookupService.getPscIndividualFullRecord("123", "12345678", "456", pscType))
//                .thenReturn(pscIndividualFullRecordApi);
//    }
//
//    @Test
//    void validate_skipsValidation_whenIdentityVerificationDetailsIsNull() {
//        when(pscIndividualFullRecordApi.getIdentityVerificationDetails()).thenReturn(null);
//
//        validator.validate(new VerificationValidationContext(pscVerificationData, errors, transaction, pscType, passthroughHeader));
//
//        assertThat(errors, is(empty()));
//        verify(logger).info(argThat(msg -> msg.contains("Validation for Extension Request Date skipped")));
//    }
//
//    @Test
//    void validate_passes_whenDueDateIsNull() {
//        var identityDetails = new IdentityVerificationDetails(null, null, null, null);
//        when(pscIndividualFullRecordApi.getIdentityVerificationDetails()).thenReturn(identityDetails);
//
//        validator.validate(new VerificationValidationContext(pscVerificationData, errors, transaction, pscType, passthroughHeader));
//
//        assertThat(errors, is(empty()));
//    }
//
//    @Test
//    void validate_passes_whenDueDateIsToday() {
//        var today = LocalDate.now();
//        var identityDetails = new IdentityVerificationDetails(null, null, null, today);
//        when(pscIndividualFullRecordApi.getIdentityVerificationDetails()).thenReturn(identityDetails);
//
//        validator.validate(new VerificationValidationContext(pscVerificationData, errors, transaction, pscType, passthroughHeader));
//
//        assertThat(errors, is(empty()));
//    }
//
//    @Test
//    void validate_addsError_whenDueDateIsInPast() {
//        var pastDate = LocalDate.now().minusDays(1);
//        var formattedDate = pastDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//        var identityDetails = new IdentityVerificationDetails(null, null, null, pastDate);
//
//        when(pscIndividualFullRecordApi.getIdentityVerificationDetails()).thenReturn(identityDetails);
//        when(validation.get("psc-cannot-verify-yet")).thenReturn("PSC cannot verify after {start-date}");
//
//        var expectedMessage = "PSC cannot verify after " + formattedDate;
//        var expectedError = new FieldError("object", "psc_extension_request_date", formattedDate, false,
//                new String[]{null, formattedDate}, null, expectedMessage);
//
//        validator.validate(new VerificationValidationContext(pscVerificationData, errors, transaction, pscType, passthroughHeader));
//
//        assertThat(errors, contains(expectedError));
//    }
//}
//
