package uk.gov.companieshouse.psc.extensions.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.LocalDateTime;
import java.util.Optional;



@Configuration
@EnableMongoRepositories("uk.gov.companieshouse.psc.extensions.api.mongo.repository")
@EnableMongoAuditing(dateTimeProviderRef = "mongodbDatetimeProvider")
public class MongoConfig {

    @Bean(name = "mongodbDatetimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }
}