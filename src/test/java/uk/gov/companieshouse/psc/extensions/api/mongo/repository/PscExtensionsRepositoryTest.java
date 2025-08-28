package uk.gov.companieshouse.psc.extensions.api.mongo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.psc.extensions.api.MongoDBTest;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class PscExtensionsRepositoryTest extends MongoDBTest {

    @Autowired
    private PscExtensionsRepository requestRepository;

    @Test
    public void When_NewRequestSaved_Expect_IdAssigned() throws URISyntaxException {
        PscExtension pscExtensionsRequest = createTestExtensionRequest();

        PscExtension savedRequest = requestRepository.save(pscExtensionsRequest);

        assertNotNull(savedRequest.toString());
        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getCreatedAt());
        assertNotNull(savedRequest.getUpdatedAt());
    }

    @Test
    public void When_RequestRetrieved_Expect_DataMatchedSavedValues() throws URISyntaxException {
        PscExtension pscExtensionsRequest = createTestExtensionRequest();

        PscExtension savedRequest = requestRepository.save(pscExtensionsRequest);

        Optional<PscExtension> retrievedPscDetail = requestRepository.findById(savedRequest.getId());

        assertTrue(retrievedPscDetail.isPresent());
        PscExtension retrieved = retrievedPscDetail.get();

        ExtensionDetails retrievedExtensionDetails = retrieved.getData().getExtensionDetails();
        assertEquals("123", retrievedExtensionDetails.getExtensionReason());
        assertEquals(LocalDate.of(2025, 1, 12), retrievedExtensionDetails.getExtensionRequestDate());
        assertEquals("done", retrievedExtensionDetails.getExtensionStatus());
    }

    private PscExtension createTestExtensionRequest() throws URISyntaxException {
        PscExtension pscExtensionsRequest =  new PscExtension();

        ResourceLinks links = new ResourceLinks(
                new URI("https://example.com"),
                new URI("https://example.com")
        );
        pscExtensionsRequest.setLinks(links);
        pscExtensionsRequest.setId("2222");
        pscExtensionsRequest.setCreatedAt(Instant.parse("2025-08-21T10:15:30.00Z"));
        pscExtensionsRequest.setUpdatedAt(Instant.now());

        Data data = new Data();
        pscExtensionsRequest.setData(data);

        ExtensionDetails extensionDetails = new ExtensionDetails();
        extensionDetails.setExtensionReason("123");
        extensionDetails.setExtensionRequestDate(LocalDate.of(2025, 1, 12));
        extensionDetails.setExtensionStatus("done");
        data.setExtensionDetails(extensionDetails);
        data.setCompanyNumber("1234");
        data.setPscNotificationId("345");

        return pscExtensionsRequest;
    }

    @Test
    public void When_UsingBuilder_Expect_ObjectCreatedCorrectly() {
        InternalData internalData = InternalData.builder().internalId("internal-123").build();
        
        PscExtension pscExtension = PscExtension.builder()
                .id("test-id")
                .createdAt(Instant.parse("2025-08-21T10:15:30.00Z"))
                .updatedAt(Instant.now())
                .internalData(internalData)
                .build();

        assertEquals("test-id", pscExtension.getId());
        assertNotNull(pscExtension.getCreatedAt());
        assertNotNull(pscExtension.getUpdatedAt());
        assertEquals("internal-123", pscExtension.getInternalData().getInternalId());
    }

    @Test
    public void When_UsingCopyConstructor_Expect_ObjectCopiedCorrectly() throws URISyntaxException {
        PscExtension original = createTestExtensionRequest();
        InternalData originalInternalData = new InternalData("copy-test-internal");
        original.setInternalData(originalInternalData);
        
        PscExtension copy = new PscExtension(original);

        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getCreatedAt(), copy.getCreatedAt());
        assertEquals(original.getUpdatedAt(), copy.getUpdatedAt());
        assertEquals(original.getLinks(), copy.getLinks());
        assertEquals(original.getData(), copy.getData());
        assertEquals(original.getInternalData(), copy.getInternalData());
    }

    @Test
    public void When_UsingEqualsAndHashCode_Expect_CorrectBehavior() throws URISyntaxException {
        InternalData internalData1 = new InternalData("same-id");
        InternalData internalData2 = new InternalData("same-id");
        InternalData internalData3 = new InternalData("different-id");

        assertEquals(internalData1, internalData2);
        assertNotEquals(internalData1, internalData3);
        assertEquals(internalData1.hashCode(), internalData2.hashCode());

        PscExtension pscExtension1 = createTestExtensionRequest();
        pscExtension1.setInternalData(internalData1);
        PscExtension pscExtension2 = new PscExtension(pscExtension1);
        pscExtension2.setInternalData(internalData2);

        assertEquals(pscExtension1, pscExtension2);
        assertEquals(pscExtension1.hashCode(), pscExtension2.hashCode());

        pscExtension2.setInternalData(internalData3);
        assertNotEquals(pscExtension1, pscExtension2);
    }

    @Test
    public void When_TestingPseNotificationId_Expect_CorrectFieldName() throws URISyntaxException {
        PscExtension pscExtension = createTestExtensionRequest();
        
        assertEquals("345", pscExtension.getData().getPscNotificationId());
        
        pscExtension.getData().setPscNotificationId("new-notification-id");
        assertEquals("new-notification-id", pscExtension.getData().getPscNotificationId());
    }

    @Test
    public void When_TestingInternalDataOperations_Expect_AllFunctionalityWorks() {
        InternalData builderData = InternalData.builder()
                .internalId("internal-123")
                .build();
        assertEquals("internal-123", builderData.getInternalId());

        InternalData original = new InternalData("original-id");
        InternalData copy = new InternalData(original);
        assertEquals(original.getInternalId(), copy.getInternalId());
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }

    @Test
    public void When_AddingInternalDataToPscExtension_Expect_StoredAndRetrievedCorrectly() throws URISyntaxException {
        InternalData internalData = InternalData.builder()
                .internalId("test-internal-123")
                .build();

        PscExtension pscExtension = createTestExtensionRequest();
        pscExtension.setInternalData(internalData);

        PscExtension savedExtension = requestRepository.save(pscExtension);
        Optional<PscExtension> retrievedOptional = requestRepository.findById(savedExtension.getId());

        assertTrue(retrievedOptional.isPresent());
        PscExtension retrieved = retrievedOptional.get();
        
        assertNotNull(retrieved.getInternalData());
        assertEquals("test-internal-123", retrieved.getInternalData().getInternalId());
    }


    @Test
    public void When_InternalDataIsNull_Expect_HandledCorrectly() throws URISyntaxException {
        PscExtension pscExtension = createTestExtensionRequest();
        // internalData should be null by default
        
        assertNull(pscExtension.getInternalData());
        
        PscExtension savedExtension = requestRepository.save(pscExtension);
        Optional<PscExtension> retrievedOptional = requestRepository.findById(savedExtension.getId());

        assertTrue(retrievedOptional.isPresent());
        assertNull(retrievedOptional.get().getInternalData());
    }
}
