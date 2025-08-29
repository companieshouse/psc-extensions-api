package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.psc.extensions.api.service.ApiClientService;
import uk.gov.companieshouse.sdk.manager.ApiClientManager;

@Service("pscExtensionsApiClientService")
public class ApiClientServiceImpl implements ApiClientService {

    public ApiClient getApiClient(String key) {
        return ApiClientManager.getSDK(key);
    }

    public InternalApiClient getInternalApiClient() {
        return ApiClientManager.getPrivateSDK();
    }
}