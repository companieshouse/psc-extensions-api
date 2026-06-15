package uk.gov.companieshouse.psc.extensions.api;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;

import java.time.Duration;

@SpringBootTest
public abstract class MongoDBTest {

    static MongoDBContainer mongoDBContainer = new MongoDBContainer(
            DockerImageName.parse("mongo:6.0.19"))
            .withStartupTimeout(Duration.ofMinutes(2));

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private PscExtensionsRepository pscExtensionsRepository;

    @AfterEach
    void tearDown() {
        pscExtensionsRepository.deleteAll();
    }
}