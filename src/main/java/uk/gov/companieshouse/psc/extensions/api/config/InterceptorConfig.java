package uk.gov.companieshouse.psc.extensions.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.api.interceptor.MappablePermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.PermissionsMapping;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.pscextensions.api.PscExtensionRequestApi;
import uk.gov.companieshouse.api.pscextensions.api.PscExtensionRequestFilingDataApi;
import uk.gov.companieshouse.api.util.security.Permission;
import uk.gov.companieshouse.psc.extensions.api.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.psc.extensions.api.utils.PathHelper;

import java.util.List;

import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;


@Configuration
@ComponentScan("uk.gov.companieshouse.api")
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull final InterceptorRegistry registry) {
        final List<String> filingPaths = PathHelper.getAllPathsFromInterfaces(PscExtensionRequestFilingDataApi.class);
        final List<String> pscExtensionPaths = PathHelper.getAllPathsFromInterfaces(PscExtensionRequestApi.class);

        registry.addInterceptor(transactionInterceptor())
                .order(1);

        registry.addInterceptor(openTransactionInterceptor())
                .addPathPatterns(filingPaths)
                .addPathPatterns(pscExtensionPaths)
                .order(2);

        registry.addInterceptor(tokenPermissionsInterceptor())
                .order(3);

        registry.addInterceptor(transactionClosedInterceptor())
                .addPathPatterns(filingPaths)
                .order(4);

        registry.addInterceptor(requestLoggingInterceptor())
                .order(5);

        registry.addInterceptor(new InternalUserInterceptor())
                .addPathPatterns(filingPaths)
                .order(6);
    }

    @Bean("chsTransactionInterceptor")
    public TransactionInterceptor transactionInterceptor() {
        return new TransactionInterceptor(APPLICATION_NAMESPACE);
    }

    @Bean("chsOpenTransactionInterceptor")
    public OpenTransactionInterceptor openTransactionInterceptor() {
        return new OpenTransactionInterceptor(APPLICATION_NAMESPACE);
    }

    @Bean("chsTokenPermissionInterceptor")
    public TokenPermissionsInterceptor tokenPermissionsInterceptor() {
        return new TokenPermissionsInterceptor();
    }


    @Bean("chsClosedTransactionInterceptor")
    public ClosedTransactionInterceptor transactionClosedInterceptor() {
        return new ClosedTransactionInterceptor(APPLICATION_NAMESPACE);
    }

   @Bean("chsLoggingInterceptor")
    public LoggingInterceptor requestLoggingInterceptor() {
        return new LoggingInterceptor();
   }
}

