package uk.gov.companieshouse.psc.extensions.api.config;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.UuidRepresentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;


class MongoConfigTest {

    private MongoConfig mongoConfig;

    @BeforeEach
    void setUp() {
        String testUri = "mongodb://localhost:27017/testdb";
        mongoConfig = new MongoConfig(testUri);
    }

    @Test
    void testDateTimeProviderReturnsCurrentTime() {
        DateTimeProvider provider = mongoConfig.dateTimeProvider();
        Optional<TemporalAccessor> dateTime = provider.getNow();
        assertTrue(dateTime.isPresent());
        assertInstanceOf(LocalDateTime.class, dateTime.get());
    }

    @Test
    void testMongoClientCreation() {
        MongoClient client = mongoConfig.mongoClient();
        assertNotNull(client);
        // Optional: check that the client was created with the expected settings
        MongoClientSettings settings = ((com.mongodb.client.internal.MongoClientImpl) client).getSettings();
        assertEquals(UuidRepresentation.STANDARD, settings.getUuidRepresentation());
        assertEquals("localhost:27017", settings.getClusterSettings().getHosts().get(0).toString());
    }
}
