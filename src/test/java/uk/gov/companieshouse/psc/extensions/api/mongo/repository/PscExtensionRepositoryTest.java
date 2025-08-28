package uk.gov.companieshouse.psc.extensions.api.mongo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.psc.extensions.api.MongoDBTest;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PscExtensionRepositoryTest extends MongoDBTest {

    @Autowired
    private PscExtensionsRepository requestRepository;

    @Test
    public void When_NewRequestSaved_Expect_IdAssigned() throws URISyntaxException {
        PscExtension pscExtensionRequest = createTestExtensionRequest();

        PscExtension savedRequest = requestRepository.save(pscExtensionRequest);

        assertNotNull(savedRequest.toString());
        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getCreatedAt());
        assertNotNull(savedRequest.getUpdatedAt());
    }

    @Test
    public void When_RequestRetrieved_Expect_DataMatchedSavedValues() throws URISyntaxException {
        PscExtension pscExtensionRequest = createTestExtensionRequest();

        PscExtension savedRequest = requestRepository.save(pscExtensionRequest);

        Optional<PscExtension> retrievedPscDetail = requestRepository.findById(savedRequest.getId());

        assertTrue(retrievedPscDetail.isPresent());
        PscExtension retrieved = retrievedPscDetail.get();

        ExtensionDetails retrievedExtensionDetails = retrieved.getData().getExtensionDetails();
        assertEquals("123", retrievedExtensionDetails.getExtensionReason());
        assertEquals("12/01/2025", retrievedExtensionDetails.getExtensionRequestDate());
        assertEquals("done", retrievedExtensionDetails.getExtensionStatus());
    }

    private PscExtension createTestExtensionRequest() throws URISyntaxException {
        PscExtension pscExtensionRequest =  new PscExtension();

        ResourceLinks links = new ResourceLinks(
                new URI("https://example.com"),
                new URI("https://example.com")
        );
        pscExtensionRequest.setLinks(links);
        pscExtensionRequest.setId("2222");
        pscExtensionRequest.setCreatedAt(LocalDateTime.parse("2025-08-21T10:15:30.00Z"));
        pscExtensionRequest.setUpdatedAt(LocalDateTime.now());

        Data data = new Data();
        pscExtensionRequest.setData(data);

        ExtensionDetails extensionDetails = new ExtensionDetails();
        extensionDetails.setExtensionReason("123");
        extensionDetails.setExtensionRequestDate("12/01/2025");
        extensionDetails.setExtensionStatus("done");
        data.setExtensionDetails(extensionDetails);
        data.setCompanyNumber("1234");
        data.setPscNotificationId("345");

        return pscExtensionRequest;
    }

}














