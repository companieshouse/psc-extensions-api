package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.psc.extensions.api.controller.FilingDataController;
import uk.gov.companieshouse.psc.extensions.api.service.FilingDataService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;

import java.util.List;

@RestController
@RequestMapping("/private/transactions/{transactionId}/persons-with-significant-control-extensions/")
public class FilingDataControllerImpl implements FilingDataController {

    private static final String PSC_EXTENSIONS_APP_NAME = "psc-extensions-api";

    private final FilingDataService filingDataService;
    private final Logger logger;

    @Autowired
    public FilingDataControllerImpl(final FilingDataService filingDataService) {
        this.filingDataService = filingDataService;
        this.logger = LoggerFactory.getLogger(PSC_EXTENSIONS_APP_NAME);
    }

    /**
     * Controller endpoint: retrieve Filing Data. Returns a list containing a single resource;
     * Future capability to return multiple resources if a Transaction contains multiple PSC
     * Extension Filings.
     *
     * @param transId        the transaction ID
     * @param filingResource the Filing Resource ID
     * @param transaction    the Transaction
     * @param request        the servlet request
     * @return List of FilingApi resources
     */
    @Override
    @GetMapping(value = "/{filingResourceId}/filings", produces = {"application/json"})
    public List<FilingApi> getFilingsData(@PathVariable("transactionId") final String transId,
                                          @PathVariable("filingResourceId") final String filingResource,
                                          @RequestAttribute(required = false, name = "transaction")
                                          Transaction transaction, final HttpServletRequest request) {

        final var logMap = LogMapHelper.createLogMap(transId, filingResource);

        logger.debugRequest(request,
                "GET /private/transactions/{transactionId}/persons-with-significant-control-extensions" +
                        "/{filingId}/filings", logMap);

        final var filingApi = filingDataService.generateFilingApi(filingResource, transaction);

        logMap.put("psc extension:", filingApi);
        logger.infoContext(transId, "psc extension data", logMap);

        return List.of(filingApi);
    }
}