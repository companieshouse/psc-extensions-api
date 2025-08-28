package uk.gov.companieshouse.psc.extensions.api.service;

import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;

public interface ApiClientService {

    ApiClient getApiClient(String key);

    InternalApiClient getInternalApiClient();
}