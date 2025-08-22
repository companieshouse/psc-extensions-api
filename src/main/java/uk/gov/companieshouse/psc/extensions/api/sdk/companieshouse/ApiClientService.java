package uk.gov.companieshouse.psc.extensions.api.sdk.companieshouse;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.sdk.manager.ApiClientManager;

@Service
public class ApiClientService {

    public ApiClient getApiClient(String key) {
        return ApiClientManager.getSDK(key);
    }

    public ApiClient getInternalApiClient() {
        return ApiClientManager.getPrivateSDK();
    }
}