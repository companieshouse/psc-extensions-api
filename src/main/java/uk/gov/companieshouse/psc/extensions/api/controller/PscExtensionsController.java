package uk.gov.companieshouse.psc.extensions.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.psc.extensions.api.exception.NotImplementedException;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsApi;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

public interface PscExtensionsController {

    /**
     * Create a new PSC Extension filing.
     *
     * @param transId     the transaction ID
     * @param transaction the transaction object
     * @param data        the extension data
     * @param result      validation binding result
     * @param request     the servlet request
     * @return ResponseEntity containing the created extension
     */
    ResponseEntity<PscExtensionsApi> createPscExtension(
            @PathVariable("transactionId") String transId,
            @RequestAttribute(required = false, name = "transaction") Transaction transaction,
            @RequestBody @Valid @NotNull PscExtensionsData data,
            BindingResult result,
            HttpServletRequest request) throws PscLookupServiceException;


    @GetMapping
    default ResponseEntity<PscExtension> getPscExtensionCount(
        @PathVariable("pscNotificationId") final String pscNotificationId) {
        throw new NotImplementedException();
    }
}
