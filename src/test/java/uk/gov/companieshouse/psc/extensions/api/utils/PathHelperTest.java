package uk.gov.companieshouse.psc.extensions.api.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.pscextensions.api.PscExtensionRequestApi;
import uk.gov.companieshouse.api.pscextensions.api.PscExtensionRequestFilingDataApi;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PathHelperTest {

    @Test
    void testGetAllPathsFromPscExtensionRequestFilingDataApi() {
        List<String> paths = PathHelper.getAllPathsFromInterfaces(PscExtensionRequestFilingDataApi.class);

        assertNotNull(paths);
        assertEquals(1, paths.size());
        assertEquals("/private/transactions/{transactionId}/persons-with-significant-control-extensions/{filingResourceId}/filings", paths.getFirst());
    }

    @Test
    void testGetAllPathsFromPscExtensionRequestApi() {
        List<String> paths = PathHelper.getAllPathsFromInterfaces(PscExtensionRequestApi.class);

        assertNotNull(paths);
        assertEquals(3, paths.size());
        Assertions.assertTrue(paths.contains("/transactions/{transactionId}/persons-with-significant-control-extensions"));
        Assertions.assertTrue(paths.contains("/persons-with-significant-control-extensions/{transactionId}/{pscNotificationId}/{companyNumber}/isPscExtensionRequestValid"));
        Assertions.assertTrue(paths.contains("/persons-with-significant-control-extensions/{pscNotificationId}/extensionCount"));
    }
}