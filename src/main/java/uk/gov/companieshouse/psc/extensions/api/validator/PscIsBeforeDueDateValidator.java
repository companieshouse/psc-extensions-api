package uk.gov.companieshouse.psc.extensions.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/*
 * Validator to check if a Person with Significant Control (PSC) can request an
 * extension providing that they apply before due date passed.
 * <p>
 * If the due date passed, an error is added to the validation context.
 * </p>
 */
@Component
public class PscIsBeforeDueDateValidator {
    private final PscLookupService pscLookupService;
    private final Logger logger;

    /**
     * Constructs a new {@code PscIsPastStartDateValidator}.
     *
     * @param validation       a map of validation messages or configurations
     * @param pscLookupService the service used to retrieve PSC details
     * @param logger           the logger for logging validation events
     */
    public PscIsBeforeDueDateValidator(Map<String, String> validation, PscLookupService pscLookupService, Logger logger) {
        super();
        this.pscLookupService = pscLookupService;
        this.logger = logger;
    }

    /**
     * Validates whether the PSC can be verified based on the appointment verification statement due date.
     * <p>
     * If the identity verification details are null, logs the event and skips validation.
     * If the due date passed, an error is added to the validation context.
     * </p>
     */
    public void validate(VerificationValidationContext validationContext) {
        PscIndividualFullRecordApi pscIndividualFullRecordApi =
                pscLookupService.getPscIndividualFullRecord(
                        validationContext.transaction().getId(),
                        validationContext.dto().companyNumber(),
                        "",
                        validationContext.pscType()
                );

        final var identityVerificationDetails = pscIndividualFullRecordApi.getIdentityVerificationDetails();

        if (identityVerificationDetails == null) {
            logger.info(String.format(
                    "Validation for PSC due date skipped due to null identity verification details. [Company number: %s, PSC notification ID: %s]",
                    validationContext.dto().companyNumber(), validationContext.dto().pscNotificationId()));
        } else {
            final var dueDate = identityVerificationDetails.appointmentVerificationStatementDate();

            if (dueDate != null && dueDate.isAfter(LocalDate.now())) {
                final var formattedStartDate = dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                // final var errorResponseText = validation.get("psc-cannot-verify-yet").replace("{due-date}", formattedStartDate);
                final var errorResponseText = "Test Error";
                validationContext.errors().add(
                        new FieldError("object", "psc_verification_due_date", formattedStartDate, false,
                                new String[]{null, formattedStartDate}, null, errorResponseText));
            }
        }
//        super.validate(validationContext);
    }
}
