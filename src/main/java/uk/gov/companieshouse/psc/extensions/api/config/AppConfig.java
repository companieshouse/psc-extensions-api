package uk.gov.companieshouse.psc.extensions.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

import java.time.Clock;

@Configuration
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    MongoTransactionManager transactionManager(final MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder().serializationInclusion(
                        JsonInclude.Include.NON_NULL)
                .simpleDateFormat("yyyy-MM-dd")
                .failOnUnknownProperties(true) // override Spring Boot default (false)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .modules(new JavaTimeModule()); // Add JSR310 module for LocalDate support
    }

    @Bean("postObjectMapper")
    @Primary
    public ObjectMapper objectMapper() {
        return objectMapperBuilder().build();
    }

    @Bean("environmentReader")
    public EnvironmentReader environmentReader() {
        return new EnvironmentReaderImpl();
    }
}