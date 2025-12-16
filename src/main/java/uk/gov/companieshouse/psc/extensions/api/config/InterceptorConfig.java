package uk.gov.companieshouse.psc.extensions.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.MappablePermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.PermissionsMapping;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.util.security.Permission;
import uk.gov.companieshouse.psc.extensions.api.interceptor.AuthenticationInterceptor;
import uk.gov.companieshouse.psc.extensions.api.interceptor.LoggingInterceptor;

import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;

@Configuration
@ComponentScan("uk.gov.companieshouse.api")
public class InterceptorConfig implements WebMvcConfigurer {



    @SuppressWarnings("java:S1075")
    public static final String BASE_PATH = "/persons-with-significant-control-extensions";
    public static final String TRANSACTION_PATH = "/transactions/{transactionId}" + BASE_PATH;

    public static final String PSC_EXTENSIONS_INTERCEPTOR_PATH = TRANSACTION_PATH;

    public static final String VALIDATION_STATUS_PATH = TRANSACTION_PATH + "/{filingResourceId}/validation_status";
    public static final String FILINGS_RESOURCE_PATH = "/private" + TRANSACTION_PATH + "/{filingResourceId}/filings";
    public static final String EXTENSIONS_COUNT_PATH = BASE_PATH + "/{pscNotificationId}/extensionCount";
    public static final String VALIDATION_PATH = BASE_PATH + "/{pscNotificationId}/{companyNumber}/isPscExtensionRequestValid";
    @Override
    public void addInterceptors(@NonNull final InterceptorRegistry registry) {

        //check for Oauth2 or internal user api key
        registry.addInterceptor(new AuthenticationInterceptor())
                .addPathPatterns(PSC_EXTENSIONS_INTERCEPTOR_PATH)
                .addPathPatterns(VALIDATION_STATUS_PATH)
                .addPathPatterns(FILINGS_RESOURCE_PATH)
                .addPathPatterns(EXTENSIONS_COUNT_PATH)
                .addPathPatterns(VALIDATION_PATH)
                .order(1);

        registry.addInterceptor(transactionInterceptor())
                .addPathPatterns(PSC_EXTENSIONS_INTERCEPTOR_PATH)
                .addPathPatterns(VALIDATION_STATUS_PATH)
                .addPathPatterns(FILINGS_RESOURCE_PATH)
                .order(2);

        registry.addInterceptor(openTransactionInterceptor())
                .addPathPatterns(FILINGS_RESOURCE_PATH)
                .addPathPatterns(VALIDATION_STATUS_PATH)
                .addPathPatterns(PSC_EXTENSIONS_INTERCEPTOR_PATH)
                .order(3);

        // this will ignore api key requests
        registry.addInterceptor(requestPermissionsInterceptor(pscPermissionsMapping()))
                .addPathPatterns(PSC_EXTENSIONS_INTERCEPTOR_PATH)
                .addPathPatterns(VALIDATION_STATUS_PATH)
                .addPathPatterns(FILINGS_RESOURCE_PATH)
                .addPathPatterns(EXTENSIONS_COUNT_PATH)
                .addPathPatterns(VALIDATION_PATH)
                .order(4);

        registry.addInterceptor(transactionClosedInterceptor())
                .addPathPatterns(FILINGS_RESOURCE_PATH)
                .order(5);

        registry.addInterceptor(requestLoggingInterceptor())
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

    @Bean("chsRequestPermissionInterceptor")
    public MappablePermissionsInterceptor requestPermissionsInterceptor(
            final PermissionsMapping permissionMapping) {
        return new MappablePermissionsInterceptor(Permission.Key.USER_PSC_VERIFICATION, true,
                permissionMapping);
    }

    @Bean("chsPermissionsMapping")
    public PermissionsMapping pscPermissionsMapping() {
        return PermissionsMapping.builder()
                .defaultRequireAnyOf(Permission.Value.CREATE)
                .build();
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
