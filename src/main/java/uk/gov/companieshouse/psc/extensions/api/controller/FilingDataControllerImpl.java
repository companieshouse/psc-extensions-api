package uk.gov.companieshouse.psc.extensions.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.api.PscExtensionRequestFilingDataApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.psc.extensions.api.service.FilingDataService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;

import java.util.List;

import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class FilingDataControllerImpl implements PscExtensionRequestFilingDataApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private final FilingDataService filingDataService;

    public FilingDataControllerImpl(final FilingDataService filingDataService) {
        this.filingDataService = filingDataService;
    }

    @Override
    public ResponseEntity<List<FilingApi>> _getFilingsData(String transactionId, String filingResourceId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        Transaction transaction = null;
        if (request != null) {
            Object attr = request.getAttribute("transaction");
            if (attr instanceof Transaction) {
                transaction = (Transaction) attr;
            }
        }

        final var logMap = LogMapHelper.createLogMap(transactionId, filingResourceId);

        LOGGER.debugRequest(request,
                "GET /private/transactions/{transactionId}/persons-with-significant-control-extensions" +
                        "/{filingId}/filings", logMap);

        final var filingApi = filingDataService.generateFilingApi(filingResourceId, transaction);

        logMap.put("psc extension:", filingApi);
        LOGGER.infoContext(transactionId, "psc extension data", logMap);

        return new ResponseEntity<>(List.of(filingApi), HttpStatus.OK);
    }
}