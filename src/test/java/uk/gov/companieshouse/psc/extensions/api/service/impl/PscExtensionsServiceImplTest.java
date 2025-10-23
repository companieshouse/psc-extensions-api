package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.psc.IdentityVerificationDetails;
import uk.gov.companieshouse.psc.extensions.api.MongoDBTest;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.validator.ExtensionRequestDateValidator;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class PscExtensionsServiceImplTest extends MongoDBTest {

    @Autowired
    private PscExtensionsService pscExtensionsService;

    @Test
    void save_WhenValidPscExtension_ShouldStoreSuccessfully() {
        PscExtension extension = createTestPscExtension();

        PscExtension savedExtension = pscExtensionsService.save(extension);

        assertNotNull(savedExtension);
        assertNotNull(savedExtension.getId());
        assertNotNull(savedExtension.getCreatedAt());
        assertNotNull(savedExtension.getUpdatedAt());
        assertEquals("12345", savedExtension.getData().getCompanyNumber());
        assertEquals("notification-123", savedExtension.getData().getPscNotificationId());
    }

    @Test
    void save_WhenExtensionWithInternalData_ShouldPreserveInternalData() {
        PscExtension extension = createTestPscExtension();
        InternalData internalData = new InternalData("internal-appointment-123");
        extension.setInternalData(internalData);

        PscExtension savedExtension = pscExtensionsService.save(extension);

        assertNotNull(savedExtension);
        assertNotNull(savedExtension.getInternalData());
        assertEquals("internal-appointment-123", savedExtension.getInternalData().getInternalId());
    }

    @Test
    void save_WhenExtensionWithLinks_ShouldPreserveLinks() throws URISyntaxException {
        PscExtension extension = createTestPscExtension();
        ResourceLinks links = new ResourceLinks(
                new URI("https://api.companieshouse.gov.uk/psc-extensions/123"),
                new URI("https://api.companieshouse.gov.uk/psc-extensions/123/self")
        );
        extension.setLinks(links);

        PscExtension savedExtension = pscExtensionsService.save(extension);

        assertNotNull(savedExtension);
        assertNotNull(savedExtension.getLinks());
        assertEquals("https://api.companieshouse.gov.uk/psc-extensions/123",
                savedExtension.getLinks().self().toString());
    }

    @Test
    void get_WhenExtensionExists_ShouldReturnExtension() {
        PscExtension extension = createTestPscExtension();
        PscExtension savedExtension = pscExtensionsService.save(extension);
        String filingId = savedExtension.getId();

        Optional<PscExtension> retrievedExtension = pscExtensionsService.get(filingId);

        assertTrue(retrievedExtension.isPresent());
        assertEquals(filingId, retrievedExtension.get().getId());
        assertEquals("12345", retrievedExtension.get().getData().getCompanyNumber());
        assertEquals("illness", retrievedExtension.get().getData().getExtensionDetails().getExtensionReason());
    }

    @Test
    void get_WhenExtensionDoesNotExist_ShouldReturnEmptyOptional() {
        String nonExistentId = "non-existent-id";

        Optional<PscExtension> retrievedExtension = pscExtensionsService.get(nonExistentId);

        assertFalse(retrievedExtension.isPresent());
    }

    @Test
    void save_WhenUpdatingExistingExtension_ShouldUpdateSuccessfully() {
        PscExtension extension = createTestPscExtension();
        PscExtension savedExtension = pscExtensionsService.save(extension);

        savedExtension.getData().setCompanyNumber("54321");
        savedExtension.getData().getExtensionDetails().setExtensionReason("death");
        savedExtension.getData().getExtensionDetails().setExtensionStatus("completed");

        PscExtension updatedExtension = pscExtensionsService.save(savedExtension);

        assertNotNull(updatedExtension);
        assertEquals(savedExtension.getId(), updatedExtension.getId());
        assertEquals("54321", updatedExtension.getData().getCompanyNumber());
        assertEquals("death", updatedExtension.getData().getExtensionDetails().getExtensionReason());
        assertEquals("completed", updatedExtension.getData().getExtensionDetails().getExtensionStatus());
    }

    @Test
    void save_WhenMultipleExtensions_ShouldHandleCorrectly() {
        PscExtension extension1 = createTestPscExtension();
        extension1.getData().setCompanyNumber("11111");

        PscExtension extension2 = createTestPscExtension();
        extension2.getData().setCompanyNumber("22222");
        extension2.getData().getExtensionDetails().setExtensionReason("death");

        PscExtension saved1 = pscExtensionsService.save(extension1);
        PscExtension saved2 = pscExtensionsService.save(extension2);

        assertNotNull(saved1);
        assertNotNull(saved2);
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());

        Optional<PscExtension> retrieved1 = pscExtensionsService.get(saved1.getId());
        Optional<PscExtension> retrieved2 = pscExtensionsService.get(saved2.getId());

        assertTrue(retrieved1.isPresent());
        assertTrue(retrieved2.isPresent());
        assertEquals("11111", retrieved1.get().getData().getCompanyNumber());
        assertEquals("22222", retrieved2.get().getData().getCompanyNumber());
        assertEquals("illness", retrieved1.get().getData().getExtensionDetails().getExtensionReason());
        assertEquals("death", retrieved2.get().getData().getExtensionDetails().getExtensionReason());
    }

    @Test
    void save_WhenExtensionWithComplexExtensionDetails_ShouldPreserveAllFields() {
        PscExtension extension = createTestPscExtension();
        ExtensionDetails extensionDetails = extension.getData().getExtensionDetails();
        extensionDetails.setExtensionReason("serious illness");
        extensionDetails.setExtensionStatus("pending review");
        extensionDetails.setExtensionRequestDate(LocalDate.of(2024, 12, 25));

        PscExtension savedExtension = pscExtensionsService.save(extension);
        Optional<PscExtension> retrievedExtension = pscExtensionsService.get(savedExtension.getId());

        assertTrue(retrievedExtension.isPresent());
        ExtensionDetails retrievedDetails = retrievedExtension.get().getData().getExtensionDetails();
        assertEquals("serious illness", retrievedDetails.getExtensionReason());
        assertEquals("pending review", retrievedDetails.getExtensionStatus());
        assertEquals(LocalDate.of(2024, 12, 25), retrievedDetails.getExtensionRequestDate());
    }

    private PscExtension createTestPscExtension() {
        PscExtension extension = new PscExtension();
        extension.setCreatedAt(Instant.now());
        extension.setUpdatedAt(Instant.now());

        Data data = new Data();
        data.setCompanyNumber("12345");
        data.setPscNotificationId("notification-123");

        ExtensionDetails extensionDetails = new ExtensionDetails();
        extensionDetails.setExtensionReason("illness");
        extensionDetails.setExtensionStatus("in_progress");
        extensionDetails.setExtensionRequestDate(LocalDate.of(2024, 1, 15));
        data.setExtensionDetails(extensionDetails);

        extension.setData(data);

        return extension;
    }

    @Test
    void validateExtensionRequest_WhenNoErrors_ShouldReturnEmptyArray() {
        try (MockedStatic<ExtensionRequestDateValidator> mockedStatic = mockStatic(ExtensionRequestDateValidator.class)) {
            mockedStatic.when(() -> ExtensionRequestDateValidator.validate(any())).thenReturn(Collections.emptySet());

            IdentityVerificationDetails idvDetails = new IdentityVerificationDetails();

            Optional<Long> extensionCount = Optional.of(1L);

            ValidationStatusError[] errors = pscExtensionsService.validateExtensionRequest(idvDetails, extensionCount);

            assertEquals(0, errors.length);
            mockedStatic.verify(() -> ExtensionRequestDateValidator.validate(idvDetails));
        }
    }

    @Test
    void validateExtensionRequest_WhenValidationErrorsPresent_ShouldReturnErrorArray() {
        try (MockedStatic<ExtensionRequestDateValidator> mockedStatic = mockStatic(ExtensionRequestDateValidator.class)) {
            ValidationStatusError error1 = new ValidationStatusError("INVALID_START_ON_DATE", "", "", "");
            ValidationStatusError error2 = new ValidationStatusError("INVALID_DUE_ON_DATE", "", "", "");
            Set<ValidationStatusError> mockErrors = new HashSet<>(Arrays.asList(error1, error2));

            mockedStatic.when(() -> ExtensionRequestDateValidator.validate(any())).thenReturn(mockErrors);

            IdentityVerificationDetails idvDetails = new IdentityVerificationDetails();

            Optional<Long> extensionCount = Optional.of(1L);

            ValidationStatusError[] errors = pscExtensionsService.validateExtensionRequest(idvDetails, extensionCount);

            assertEquals(2, errors.length, "Expected 2 validation errors");
            List<String> errorCodes = Arrays.stream(errors).map(ValidationStatusError::getError).toList();
            assertTrue(errorCodes.contains("INVALID_START_ON_DATE"), "Missing INVALID_START_ON_DATE");
            assertTrue(errorCodes.contains("INVALID_DUE_ON_DATE"), "Missing INVALID_DUE_ON_DATE");

            mockedStatic.verify(() -> ExtensionRequestDateValidator.validate(idvDetails));
        }
    }
}
