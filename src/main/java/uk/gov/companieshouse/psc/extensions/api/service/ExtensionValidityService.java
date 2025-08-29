package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;

public interface ExtensionValidityService {

    /**
     * Validates whether a PSC is allowed to submit another extension request.
     * PSCs can submit a maximum of two extension requests.
     *
     * @param pscExtensionsData the extension data containing PSC details
     * @return true if the PSC can submit another extension request, false otherwise
     * @throws RuntimeException if validation cannot be completed
     */
    boolean canSubmitExtensionRequest(PscExtensionsData pscExtensionsData);
}