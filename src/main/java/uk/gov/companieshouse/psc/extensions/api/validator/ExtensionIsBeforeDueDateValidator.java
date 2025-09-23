package uk.gov.companieshouse.psc.extensions.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Validator to check if an Extension can be requested based
 * on the appointment verification statement due date.
 * <p>
 * If the due date is before the request date, an error is added to the validation context.
 * </p>
 */
@Component
public class ExtensionIsBeforeDueDateValidator {
    public Map<String, String> validation;
    private final PscLookupService pscLookupService;
    private final Logger logger;

    /**
     * Constructs a new {@code ExtensionIsBeforeDueDateValidator}.
     *
     * @param pscLookupService the service used to retrieve PSC details
     * @param logger the logger for logging validation events
     */
    public ExtensionIsBeforeDueDateValidator(PscLookupService pscLookupService, Logger logger) {
        this.pscLookupService = pscLookupService;
        this.logger = logger;
    }

    /**
     * Validates whether the Extension can be requested based on the appointment verification statement due date.
     * <p>
     * If the identity verification details are null, logs the event and skips validation.
     * If the due date is before the request date, adds a validation error.
     * </p>
     */
    public void validate(VerificationValidationContext validationContext) {
        PscIndividualFullRecordApi pscIndividualFullRecordApi = pscLookupService.getPscIndividualFullRecord(
                validationContext.transaction().getId(), validationContext.dto().companyNumber(),
                validationContext.dto().pscNotificationId(), validationContext.pscType());

        final var identityVerificationDetails = pscIndividualFullRecordApi.getIdentityVerificationDetails();

        if (identityVerificationDetails == null) {
            logger.info(String.format(
                    "Validation for Extension Request Date skipped due to null identity verification details. [Company number: %s, PSC notification ID: %s]",
                    validationContext.dto().companyNumber(), validationContext.dto().pscNotificationId()));
        } else {
            final var dueDate = identityVerificationDetails.appointmentVerificationStatementDueOn();

            final var requestDate = LocalDate.now();
            if (dueDate != null && requestDate.isAfter(dueDate)) {
                final var formattedDueDate = dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                // TODO: does psc-cannot-verify-yet need to be changed to an error response relevant to Extension request?
                final var errorResponseText = validation.get("psc-cannot-verify-yet").replace("{start-date}", formattedDueDate);
                validationContext.errors().add(
                        new FieldError("object", "psc_extension_request_date", formattedDueDate, false,
                                new String[] { null, formattedDueDate }, null, errorResponseText));
            }
        }
    }
}
