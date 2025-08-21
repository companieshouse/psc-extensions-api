package uk.gov.companieshouse.psc.extensions.api;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;

import java.time.Duration;

@Testcontainers(disabledWithoutDocker = true)
public abstract class MongoDBTest {

    private static final DockerImageName MONGO_IMAGE = DockerImageName.parse("mongo:6.0.19");

    private static final MongoDBContainer mongoDBContainer;

    static {
        mongoDBContainer = new MongoDBContainer(MONGO_IMAGE)
                .withStartupTimeout(Duration.ofMinutes(2));
        mongoDBContainer.start();
    }

    @Autowired
    private PscExtensionsRepository pscExtensionsRepository;

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @AfterEach
    void tearDown() {
        pscExtensionsRepository.deleteAll();
    }
}
