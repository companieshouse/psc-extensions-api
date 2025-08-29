package uk.gov.companieshouse.psc.extensions.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.util.List;

public interface FilingDataController {

    /**
     * Retrieve Filing Data for PSC Extensions.
     * Called by filing-resource-handler to get filing data for CHIPS processing.
     *
     * @param transId        the transaction ID
     * @param filingResource the Filing Resource ID
     * @param transaction    the Transaction
     * @param request        the servlet request
     * @return List of FilingApi resources
     */
    List<FilingApi> getFilingsData(
            @PathVariable("transactionId") String transId,
            @PathVariable("filingResourceId") String filingResource,
            @RequestAttribute(required = false, name = "transaction") Transaction transaction,
            HttpServletRequest request
    );
}