package uk.gov.companieshouse.psc.extensions.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@EnableMongoRepositories("uk.gov.companieshouse.psc.extensions.api.mongo.repository")
public class MongoConfig {

    @Bean( name = "mongodbDatetimeProvider" )
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of( LocalDateTime.now() );
    }
}
