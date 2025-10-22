package uk.gov.companieshouse.psc.extensions.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.pscextensions.api.PscExtensionValidationStatusApi;
import uk.gov.companieshouse.api.pscextensions.model.ValidationStatusResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;


import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class ValidationStatusControllerImpl implements PscExtensionValidationStatusApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);
    private final PscExtensionsControllerImpl pscExtensionsController;
    private final PscLookupService pscLookupService;
    private final PscExtensionsService pscExtensionsService;

    public ValidationStatusControllerImpl(final PscExtensionsControllerImpl pscExtensionsController,
                                          final PscLookupService pscLookupService,
                                          final PscExtensionsService pscExtensionsService) {
        this.pscExtensionsController = pscExtensionsController;
        this.pscLookupService = pscLookupService;
        this.pscExtensionsService = pscExtensionsService;
    }

    @Override
    public ResponseEntity<ValidationStatusResponse> _validate(@PathVariable("transactionId") final String transactionId,
                                                              @PathVariable("filingResourceId") final String filingResourceId)
    {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        final var logMap = LogMapHelper.createLogMap(transactionId);
        logMap.put("path", request.getRequestURI());
        logMap.put("method", request.getMethod());
        LOGGER.debugRequest(request, "GET validation request", logMap);

        final var pscExtensionOpt = pscExtensionsService.get(filingResourceId);
        final var pscExtension = pscExtensionOpt.orElseThrow(() -> new FilingResourceNotFoundException(
                String.format("PSC extension not found when generating filing for %s", filingResourceId)));

        final var companyNumber = pscExtension.getData().getCompanyNumber();
        final var pscNotificationId = pscExtension.getData().getPscNotificationId();

        final PscIndividualFullRecordApi pscIndividualFullRecordApi;
        try {
            pscIndividualFullRecordApi = pscLookupService.getPscIndividualFullRecord(
                    companyNumber,
                    pscNotificationId,
                    PscType.INDIVIDUAL
            );
        } catch (PscLookupServiceException e) {
            logMap.put("psc_notification_id", pscNotificationId);
            LOGGER.errorContext(String.format("PSC Id %s does not have an Internal ID in PSC Data API for company number %s",
                    pscNotificationId, companyNumber), null, logMap);
            throw new PscLookupServiceException(
                    "We are currently unable to process an Extension filing for this PSC", new Exception("Internal Id"));
        }

        var validationStatus = pscExtensionsController.getValidationStatus(pscNotificationId, pscIndividualFullRecordApi);

        return ResponseEntity.ok(validationStatus);
    }
}
