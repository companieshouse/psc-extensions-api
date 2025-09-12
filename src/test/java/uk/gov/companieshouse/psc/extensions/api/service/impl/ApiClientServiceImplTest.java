package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiClientManager;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class ApiClientServiceImplTest {

    @Test
    void getApiClientReturnsSdkInstance() {
        try (MockedStatic<ApiClientManager> clientManager = mockStatic(ApiClientManager.class)) {
            ApiClient mockClient = mock(ApiClient.class);
            clientManager.when(() -> ApiClientManager.getSDK("")).thenReturn(mockClient);

            ApiClientServiceImpl service = new ApiClientServiceImpl();
            assertSame(mockClient, service.getApiClient(""));
        }
    }

    @Test
    void getInternalApiClientReturnsPrivateSdkInstance() {
            try (MockedStatic<ApiClientManager> clientManager = mockStatic(ApiClientManager.class)) {
                InternalApiClient mockClient = mock(InternalApiClient.class);
                clientManager.when(ApiClientManager::getPrivateSDK).thenReturn(mockClient);

                ApiClientServiceImpl service = new ApiClientServiceImpl();
                assertSame(mockClient, service.getInternalApiClient());
            }
        }
}
