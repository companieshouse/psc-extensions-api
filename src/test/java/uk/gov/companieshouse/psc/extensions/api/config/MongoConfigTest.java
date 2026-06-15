package uk.gov.companieshouse.psc.extensions.api.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;


class MongoConfigTest {

    private MongoConfig mongoConfig;

    @BeforeEach
    void setUp() {
        String testUri = "mongodb://localhost:27017/testdb";
        mongoConfig = new MongoConfig();
    }

    @Test
    void testDateTimeProviderReturnsCurrentTime() {
        DateTimeProvider provider = mongoConfig.dateTimeProvider();
        Optional<TemporalAccessor> dateTime = provider.getNow();
        assertTrue(dateTime.isPresent());
        assertInstanceOf(LocalDateTime.class, dateTime.get());
    }
}