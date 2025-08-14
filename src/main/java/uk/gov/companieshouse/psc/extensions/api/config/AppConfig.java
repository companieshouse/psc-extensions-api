package uk.gov.companieshouse.psc.extensions.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

@Configuration
//@EnableTransactionManagement
public class AppConfig {

  @Bean("environmentReader")
  public EnvironmentReader environmentReader() {
    return new EnvironmentReaderImpl();
  }
}
