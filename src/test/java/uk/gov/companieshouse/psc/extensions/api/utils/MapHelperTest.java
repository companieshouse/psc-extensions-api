package uk.gov.companieshouse.psc.extensions.api.utils;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapHelperTest {
    private static final String APPOINTMENT_ID = "test-appointment-id";

    @Test
    void convertObjectTest() {
        final var testPscExtension = createTestPscExtension();

        Map<String,Object> result = MapHelper.convertObject(testPscExtension);

        assertTrue(result.containsKey("data"));
        Map<String, Object> dataMap = (Map<String, Object>) result.get("data");
        assertEquals("12345", dataMap.get("company_number"));
        assertEquals("notification-123", dataMap.get("psc_notification_id"));
        assertEquals("2024-01-15", ((Map<String, Object>)dataMap.get("extension_details")).get("extension_request_date"));
    }

    private PscExtension createTestPscExtension() {
        PscExtension extension = new PscExtension();

        InternalData internalData = new InternalData();
        internalData.setInternalId(APPOINTMENT_ID);
        extension.setInternalData(internalData);

        Data data = new Data();
        data.setCompanyNumber("12345");
        data.setPscNotificationId("notification-123");

        ExtensionDetails extensionDetails = new ExtensionDetails();
        extensionDetails.setExtensionStatus("individual");
        extensionDetails.setExtensionReason("illness");
        extensionDetails.setExtensionRequestDate(LocalDate.of(2024, 1, 15));
        data.setExtensionDetails(extensionDetails);

        extension.setData(data);

        return extension;
    }
}
