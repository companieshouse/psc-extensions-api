package uk.gov.companieshouse.psc.extensions.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.api.PscExtensionRequestFilingDataApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.exception.TransactionNotFoundException;
import uk.gov.companieshouse.psc.extensions.api.service.FilingDataService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;

import java.util.List;

@RestController
public class FilingDataControllerImpl implements PscExtensionRequestFilingDataApi {

    private final FilingDataService filingDataService;
    private final Logger logger;

    public FilingDataControllerImpl(final FilingDataService filingDataService, final Logger logger) {
        this.filingDataService = filingDataService;
        this.logger = logger;
    }

    @Override
    public ResponseEntity<List<FilingApi>> _getFilingsData(String transactionId, String filingResourceId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

      Transaction transaction = Optional.of(request)
          .map(r -> r.getAttribute("transaction"))
          .filter(Transaction.class::isInstance)
          .map(Transaction.class::cast)
          .orElseThrow(() -> new TransactionNotFoundException("Transaction not found in request attributes"));

      final var logMap = LogMapHelper.createLogMap(transactionId, filingResourceId);

        logger.debugRequest(request,
                "GET /private/transactions/{transactionId}/persons-with-significant-control-extensions" +
                        "/{filingId}/filings", logMap);

        final var filingApi = filingDataService.generateFilingApi(filingResourceId, transaction);

        logMap.put("psc extension:", filingApi);
        logger.infoContext(transactionId, "psc extension data", logMap);

        return new ResponseEntity<>(List.of(filingApi), HttpStatus.OK);
    }
}
