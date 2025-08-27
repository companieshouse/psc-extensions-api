package uk.gov.companieshouse.psc.extensions.api.mongo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.api.officer.NameElements;
import uk.gov.companieshouse.psc.extensions.api.MongoDBTest;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtensions;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PscExtensionsRepositoryTest extends MongoDBTest {

    @Autowired
    private PscExtensionsRepository requestRepository;

    @Test
    public void When_NewRequestSaved_Expect_IdAssigned() throws URISyntaxException {
        PscExtensions pscExtensionsRequest = createTestExtensionRequest();

        PscExtensions savedRequest = requestRepository.save(pscExtensionsRequest);

        assertNotNull(savedRequest.toString());
        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getCreatedAt());
        assertNotNull(savedRequest.getUpdatedAt());
    }

    @Test
    public void When_RequestRetrieved_Expect_DataMatchedSavedValues() throws URISyntaxException {
        PscExtensions pscExtensionsRequest = createTestExtensionRequest();

        PscExtensions savedRequest = requestRepository.save(pscExtensionsRequest);

        Optional<PscExtensions> retrievedPscDetail = requestRepository.findById(savedRequest.getId());

        assertTrue(retrievedPscDetail.isPresent());
        PscExtensions retrieved = retrievedPscDetail.get();

        ExtensionDetails retrievedExtensionDetails = retrieved.getData().getExtensionDetails();
        assertEquals("123", retrievedExtensionDetails.getExtensionReason());
        assertEquals("12/01/2025", retrievedExtensionDetails.getExtensionRequestDate());
        assertEquals("done", retrievedExtensionDetails.getExtensionStatus());
    }

    private PscExtensions createTestExtensionRequest() throws URISyntaxException {
        PscExtensions pscExtensionsRequest =  new PscExtensions();

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
        extensionDetails.setExtensionRequestDate("12/01/2025");
        extensionDetails.setExtensionStatus("done");
        data.setExtensionDetails(extensionDetails);
        data.setCompanyNumber("1234");
        data.setPscAppointmentId("345");

        return pscExtensionsRequest;
    }

}














