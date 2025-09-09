package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.service.ExtensionValidityService;

@Service
public class ExtensionValidityServiceImpl implements ExtensionValidityService {

    @Override
    public boolean canSubmitExtensionRequest(PscExtensionsData pscExtensionsData) {
        // TODO: Integrate with this PR - https://github.com/companieshouse/psc-extensions-api/pull/20.
        return true;
    }
}