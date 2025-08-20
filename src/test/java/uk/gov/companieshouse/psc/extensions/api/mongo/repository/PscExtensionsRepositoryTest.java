package uk.gov.companieshouse.psc.extensions.api.mongo.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.api.officer.NameElements;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtensions;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.RelevantOfficer;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class PscExtensionsRepositoryTest {

    private static final DockerImageName MONGO_IMAGE = DockerImageName.parse("mongo:6.0.19");

    private static final MongoDBContainer mongoDBContainer;

    static {
        mongoDBContainer = new MongoDBContainer(MONGO_IMAGE)
                .withStartupTimeout(Duration.ofMinutes(2));
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private PscExtensionsRepository requestRepository;

    @Test
    public void When_NewRequestSaved_Expect_IdAssigned() throws URISyntaxException {
        PscExtensions pscExtensions =  new PscExtensions();
        ResourceLinks links = new ResourceLinks(new URI("https://google.com"), new URI("https://google.com"));
        pscExtensions.setLinks(links);
        Data data = new Data();
        pscExtensions.setData(data);
        ExtensionDetails extensionDetails = new ExtensionDetails();
        extensionDetails.setExtensionReason("123");
        extensionDetails.setExtensionRequestDate("12/01/2025");
        extensionDetails.setExtensionStatus("done");
        data.setExtensionDetails(extensionDetails);
        RelevantOfficer relevantOfficer = new RelevantOfficer();
        data.setRelevantOfficer(relevantOfficer);
        relevantOfficer.setDateOfBirth("12/01/1970");
        NameElements nameElements = new NameElements();
        nameElements.setForename("Frank");
        nameElements.setSurname("Clark");
        nameElements.setTitle("Mr");
        nameElements.setOtherForenames("Sinatra");
        relevantOfficer.setNameElements(nameElements);
        PscExtensions savedRequest = requestRepository.save(pscExtensions);

        assertNotNull(savedRequest.toString());
        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getCreatedAt());
        assertNotNull(savedRequest.getUpdatedAt());
    }

}