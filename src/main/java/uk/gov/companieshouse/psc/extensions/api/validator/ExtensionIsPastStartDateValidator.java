package uk.gov.companieshouse.psc.extensions.api.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;

/**
 * Validator to check if an Extension can be requested based
 * on the appointment verification statement start date.
 * <p>
 * If the start date is in the future, an error is added to the validation context.
 * </p>
 */
@Component
public class ExtensionIsPastStartDateValidator {
    protected Map<String, String> validation;
    private final PscLookupService pscLookupService;
    private final Logger logger;

    /**
     * Constructs a new {@code ExtensionIsPastStartDateValidator}.
     *
     * @param pscLookupService the service used to retrieve PSC details
     * @param logger the logger for logging validation events
     */
    public ExtensionIsPastStartDateValidator(PscLookupService pscLookupService, Logger logger) {
        this.pscLookupService = pscLookupService;
        this.logger = logger;
    }

    /**
     * Validates whether the Extension can be requested based on the appointment verification statement start date.
     * <p>
     * If the identity verification details are null, logs the event and skips validation.
     * If the start date is in the future, adds a validation error.
     * </p>
     */
    public void validate(VerificationValidationContext validationContext) {
        PscIndividualFullRecordApi pscIndividualFullRecordApi = pscLookupService.getPscIndividualFullRecord(
                validationContext.transaction().getId(), validationContext.dto().companyNumber(),
                validationContext.dto().pscNotificationId(), validationContext.pscType());

        final var identityVerificationDetails = pscIndividualFullRecordApi.getIdentityVerificationDetails();

        if (identityVerificationDetails == null) {
            logger.info(String.format(
                    "Validation for Extension Request Date date skipped due to null identity verification details. [Company number: %s, PSC notification ID: %s]",
                    validationContext.dto().companyNumber(), validationContext.dto().pscNotificationId()));
        } else {
            final var startDate = identityVerificationDetails.appointmentVerificationStatementDate();

            final var requestDate = LocalDate.now();
            if (startDate != null && requestDate.isBefore(startDate)) {
                final var formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                // TODO: does psc-cannot-verify-yet need to be changed to an error response relevant to Extension request?
                final var errorResponseText = validation.get("psc-cannot-verify-yet").replace("{start-date}", formattedStartDate);
                validationContext.errors().add(
                        new FieldError("object", "psc_verification_start_date", formattedStartDate, false,
                                new String[] { null, formattedStartDate }, null, errorResponseText));
            }
        }
    }
}
