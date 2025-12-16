package uk.gov.companieshouse.psc.extensions.api.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.psc.IdentityVerificationDetails;

@ExtendWith(MockitoExtension.class)
class ExtensionRequestDateValidatorTest {

    @Test
    void validate_When_PscStatementDateIsNull_Expect_Error() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate futureDueDate = LocalDate.now().plusDays(10);

        Mockito.when(details.getAppointmentVerificationStatementDate()).thenReturn(null);
        Mockito.when(details.getAppointmentVerificationStatementDueOn()).thenReturn(futureDueDate);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.stream()
                .anyMatch(e -> e.getError().contains(
                        "Missing IDV start date or due date")));
    }

    @Test
    void validate_When_PscDueDateIsNull_Expect_Error() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate pastStartDate = LocalDate.now().minusDays(3);

        Mockito.when(details.getAppointmentVerificationStatementDate()).thenReturn(pastStartDate);
        Mockito.when(details.getAppointmentVerificationStatementDueOn()).thenReturn(null);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.stream()
                .anyMatch(e -> e.getError().contains(
                        "Missing IDV start date or due date")));
    }

    @Test
    void validate_When_RequestDateIsBeforeStartDate_Expect_Error() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate futureStartDate = LocalDate.now().plusDays(5);
        LocalDate futureDueDate = LocalDate.now().plusDays(10);

        Mockito.when(details.getAppointmentVerificationStatementDate()).thenReturn(futureStartDate);
        Mockito.when(details.getAppointmentVerificationStatementDueOn()).thenReturn(futureDueDate);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.stream()
                .anyMatch(e -> e.getError().contains(
                        "A PSC cannot request an extension before the IDV Start Date")));
    }

    @Test
    void validate_When_RequestDateIsAfterDueDate_Expect_Error() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate pastStartDate = LocalDate.now().minusDays(3);
        LocalDate pastDueDate = LocalDate.now().minusDays(1);

        Mockito.when(details.getAppointmentVerificationStatementDate()).thenReturn(pastStartDate);
        Mockito.when(details.getAppointmentVerificationStatementDueOn()).thenReturn(pastDueDate);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.stream()
                .anyMatch(e -> e.getError().contains(
                        "A PSC cannot request an extension after the IDV Due Date")));
    }

    @Test
    void validate_When_RequestDateIsBeforeStartDateAndAfterDueDate_Expect_TwoErrors() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate futureStartDate = LocalDate.now().plusDays(5);
        LocalDate pastDueDate = LocalDate.now().minusDays(1);

        Mockito.when(details.getAppointmentVerificationStatementDate()).thenReturn(futureStartDate);
        Mockito.when(details.getAppointmentVerificationStatementDueOn()).thenReturn(pastDueDate);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertEquals(2, errors.size());
    }

    @Test
    void validate_When_RequestDateIsWithinValidRange_Expect_SuccessfulRequest() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate dueDate = LocalDate.now().plusDays(1);
        Mockito.when(details.getAppointmentVerificationStatementDate()).thenReturn(startDate);
        Mockito.when(details.getAppointmentVerificationStatementDueOn()).thenReturn(dueDate);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    void validate_When_DatesAreNull_Expect_NoErrors() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate pastStartDate = LocalDate.now().minusDays(3);
        LocalDate futureDueDate = LocalDate.now().plusDays(10);

        Mockito.when(details.getAppointmentVerificationStatementDate()).thenReturn(pastStartDate);
        Mockito.when(details.getAppointmentVerificationStatementDueOn()).thenReturn(futureDueDate);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    void validate_When_IdvDetailsIsNull_Expect_Error() {
        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(null);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.stream()
                .anyMatch(e -> e.getError().contains("Missing identity verification details")));
    }
}
