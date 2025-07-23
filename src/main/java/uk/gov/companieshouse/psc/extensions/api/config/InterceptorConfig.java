package uk.gov.companieshouse.psc.extensions.api.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.psc.extensions.api.interceptor.LoggingInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
  private final LoggingInterceptor loggingInterceptor;
  public InterceptorConfig(final LoggingInterceptor loggingInterceptor ) {
    this.loggingInterceptor = loggingInterceptor;
  }
  @Override
  public void addInterceptors( final InterceptorRegistry registry ) {
    registry.addInterceptor( loggingInterceptor );
  }
}