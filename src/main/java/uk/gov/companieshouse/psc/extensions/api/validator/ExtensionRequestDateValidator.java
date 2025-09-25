package uk.gov.companieshouse.psc.extensions.api.validator;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator to check if an Extension Request is valid based on
 * the appointment verification statement date and due date.
 */
@Component
public class ExtensionRequestDateValidator {

    /**
     * Validates whether the Extension Request is valid based on
     * the appointment verification statement date and due date.
     * <p>
     * If the identity verification details are null, logs the event and skips validation
     * If the request date is before the statement date, adds a validation error.
     * If the request data is after the due date, adds a validation error.
     * </p>
     */
    public static Set<ValidationStatusError> validate(IdentityVerificationDetails idvDetails) {
        final Set<ValidationStatusError> errors = new HashSet<>();

        final var requestDate = LocalDate.now();
        final var startDate = idvDetails.appointmentVerificationStatementDate();
        final var dueDate = idvDetails.appointmentVerificationStatementDueOn();

        if (startDate != null && requestDate.isBefore(startDate)) {
            final var formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            final var errorResponseText = String.format("A PSC cannot request an extension before the IDV Start Date %s", formattedStartDate);
            errors.add(new ValidationStatusError(errorResponseText, "$.psc_verification_start_date", "json-path", "ch:validation"));
        }

        if (dueDate != null && requestDate.isAfter(dueDate)) {
            final var formattedDueDate = dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            final var errorResponseText = String.format("A PSC cannot request an extension after the IDV Due Date %s", formattedDueDate);
            errors.add(new ValidationStatusError(errorResponseText, "$.psc_verification_due_date", "json-path", "ch:validation"));
        }

        return errors;
    }
}
