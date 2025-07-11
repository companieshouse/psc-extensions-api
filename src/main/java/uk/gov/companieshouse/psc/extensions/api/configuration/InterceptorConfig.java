package uk.gov.companieshouse.psc.extensions.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.psc.extensions.api.interceptor.RequestLifecycleInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final RequestLifecycleInterceptor requestLifecycleInterceptor;

    public InterceptorConfig(final RequestLifecycleInterceptor requestLifecycleInterceptor ) {
        this.requestLifecycleInterceptor = requestLifecycleInterceptor;
    }

    @Override
    public void addInterceptors( final InterceptorRegistry registry ) {
        registry.addInterceptor( requestLifecycleInterceptor );
    }

}
