package uk.gov.companieshouse.psc.extensions.api.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;


@ExtendWith(MockitoExtension.class)
class ExtensionRequestDateValidatorTest {


    @Test
    void When_RequestDateIsBeforeStartDate_Expect_Error() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate futureStartDate = LocalDate.now().plusDays(5);
        LocalDate requestDate = LocalDate.now();

        Mockito.when(details.appointmentVerificationStatementDate()).thenReturn(futureStartDate);
        Mockito.when(details.appointmentVerificationStatementDueOn()).thenReturn(null);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.stream()
                .anyMatch(e -> e.getError().contains("A PSC cannot request an extension before the IDV Start Date")));

    }

    @Test
    void When_RequestDateIsAfterDueDate_Expect_Error() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate pastDueDate = LocalDate.now().minusDays(1);
        LocalDate requestDate = LocalDate.now();

        Mockito.when(details.appointmentVerificationStatementDueOn()).thenReturn(pastDueDate);
        Mockito.when(details.appointmentVerificationStatementDate()).thenReturn(null);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.stream()
                .anyMatch(e -> e.getError().contains("A PSC cannot request an extension after the IDV Due Date")));
    }

    @Test
    void When_RequestDateIsBeforeStartDateAndAfterDueDate_Expect_TwoErrors() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate futureStartDate = LocalDate.now().plusDays(5);
        LocalDate pastDueDate = LocalDate.now().minusDays(1);
        Mockito.when(details.appointmentVerificationStatementDate()).thenReturn(futureStartDate);
        Mockito.when(details.appointmentVerificationStatementDueOn()).thenReturn(pastDueDate);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertEquals(2, errors.size());
    }

    @Test
    void When_RequestDateIsWithinValidRange_Expect_SuccessfulRequest() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate dueDate = LocalDate.now().plusDays(1);
        Mockito.when(details.appointmentVerificationStatementDate()).thenReturn(startDate);
        Mockito.when(details.appointmentVerificationStatementDueOn()).thenReturn(dueDate);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    void When_DatesAreNull_Expect_NoErrors() {
        IdentityVerificationDetails details = Mockito.mock(IdentityVerificationDetails.class);
        Mockito.when(details.appointmentVerificationStatementDate()).thenReturn(null);
        Mockito.when(details.appointmentVerificationStatementDueOn()).thenReturn(null);

        Set<ValidationStatusError> errors = ExtensionRequestDateValidator.validate(details);

        Assertions.assertTrue(errors.isEmpty());
    }
}

