package uk.gov.companieshouse.psc.extensions.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@EnableMongoRepositories( "uk.gov.companieshouse.psc.extensions.repositories" ) //TODO: Create this folder
@EnableMongoAuditing( dateTimeProviderRef = "mongodbDatetimeProvider" )
public class MongoConfig {

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener( final LocalValidatorFactoryBean factory ) {
        return new ValidatingMongoEventListener( factory );
    }

    @Bean( name = "mongodbDatetimeProvider" )
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of( LocalDateTime.now() );
    }

}
