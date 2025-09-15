package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.psc.extensions.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.psc.extensions.api.model.FilingKind;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingDataServiceImplTest {

    private final String FILING_ID = "test-filing-id";
    private final String APPOINTMENT_ID = "test-appointment-id";
    @Mock
    private PscExtensionsService pscExtensionsService;
    @InjectMocks
    private FilingDataServiceImpl filingDataService;
    private PscExtension testPscExtension;
    @Mock
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testPscExtension = createTestPscExtension();
        testTransaction = new Transaction();
        testTransaction.setId("test-transaction-id");
    }

    @Test
    void generateFilingApi_WhenPscExtensionExists_ShouldReturnValidFilingApi() {
        when(pscExtensionsService.get(FILING_ID)).thenReturn(Optional.of(testPscExtension));

        FilingApi result = filingDataService.generateFilingApi(FILING_ID, testTransaction);

        assertNotNull(result);
        assertEquals(FilingKind.FULL_KIND, result.getKind());
        assertEquals("Extension request for PSC verification deadline", result.getDescription());
        assertNotNull(result.getData());

        Map<String, Object> dataMap = result.getData();
        assertTrue(dataMap.containsKey("appointment_id"));
        assertEquals(APPOINTMENT_ID, dataMap.get("appointment_id"));

        assertEquals("12345", dataMap.get("company_number"));

        Map<String, Object> extensionDetails = (Map<String, Object>) dataMap.get("extension_details");
        assertNotNull(extensionDetails);
        assertEquals("individual", extensionDetails.get("extension_status"));
        assertEquals("illness", extensionDetails.get("extension_reason"));

        assertNull(dataMap.get("psc_notification_id"));
    }

    @Test
    void generateFilingApi_WhenPscExtensionNotFound_ShouldThrowException() {
        when(pscExtensionsService.get(anyString())).thenReturn(Optional.empty());

        FilingResourceNotFoundException exception = assertThrows(
                FilingResourceNotFoundException.class,
                () -> filingDataService.generateFilingApi(FILING_ID, testTransaction)
        );

        assertEquals("PSC extension not found when generating filing for test-filing-id", exception.getMessage());
    }

    @Test
    void generateFilingApi_WithNullInternalData_ShouldHandleGracefully() {
        testPscExtension.setInternalData(null);
        when(pscExtensionsService.get(FILING_ID)).thenReturn(Optional.of(testPscExtension));

        assertThrows(
                RuntimeException.class,
                () -> filingDataService.generateFilingApi(FILING_ID, testTransaction)
        );
    }

    @Test
    void generateFilingApi_WithNullData_ShouldHandleGracefully() {
        testPscExtension.setData(null);
        when(pscExtensionsService.get(FILING_ID)).thenReturn(Optional.of(testPscExtension));

        assertThrows(
                RuntimeException.class,
                () -> filingDataService.generateFilingApi(FILING_ID, testTransaction)
        );
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