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
//import uk.gov.companieshouse.logging.Logger;
//import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
//import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
//
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
///*import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.collection.IsEmptyCollection.empty;*/
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ExtensionIsPastStartDateValidatorTest {
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
//    private ExtensionIsPastStartDateValidator validator;
//    private Set<FieldError> errors;
//    private PscType pscType;
//    private String passthroughHeader;
//
//    @BeforeEach
//    void setUp() {
//        validator = new ExtensionIsPastStartDateValidator(pscLookupService, logger);
//        validator.validation = validation; // Inject the validation map
//
//        errors = new HashSet<>();
//        pscType = PscType.INDIVIDUAL;
//        passthroughHeader = "passthroughHeader";
//
//        lenient().when(pscLookupService.getPscIndividualFullRecord("123", "12345678", "456", pscType))
//                .thenReturn(pscIndividualFullRecordApi);
//    }
//
//    /*@Test
//    void validate_skipsValidation_whenIdentityVerificationDetailsIsNull() {
//        when(pscIndividualFullRecordApi.getIdentityVerificationDetails()).thenReturn(null);
//
//        validator.validate(new VerificationValidationContext(pscVerificationData, errors, transaction, pscType, passthroughHeader));
//
//        assertThat(errors, is(empty()));
//        verify(logger).info(argThat(msg -> msg.contains("Validation for Extension Request Date skipped")));
//
//    }*/
//
//    @Test
//    void validate_passes_whenStartDateIsNull() {
//        var identityDetails = new IdentityVerificationDetails(null, null, null, null);
//
//    }
//}
