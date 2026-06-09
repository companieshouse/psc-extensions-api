package uk.gov.companieshouse.psc.extensions.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

import java.text.SimpleDateFormat;
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
    public JsonMapperBuilderCustomizer objectMapperBuilder() {
        return builder ->
            builder.changeDefaultPropertyInclusion(incl -> incl
                            .withContentInclusion(JsonInclude.Include.NON_NULL)
                            .withValueInclusion(JsonInclude.Include.NON_NULL))
                    .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                    .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    }


    @Bean("environmentReader")
    public EnvironmentReader environmentReader() {
        return new EnvironmentReaderImpl();
    }
}