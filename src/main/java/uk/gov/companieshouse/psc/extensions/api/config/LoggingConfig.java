package uk.gov.companieshouse.psc.extensions.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Configuration class for CH logging.
 */
@Configuration
@PropertySource("classpath:logger.properties")
public class LoggingConfig {

    @Value("${logger.namespace}")
    private String loggerNamespace;

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(loggerNamespace);
    }
}