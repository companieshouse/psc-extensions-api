package uk.gov.companieshouse.psc.extensions.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.psc.extensions.api.interceptor.LoggingInterceptor;

import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;


@Configuration
@ComponentScan("uk.gov.companieshouse.api")
public class InterceptorConfig implements WebMvcConfigurer {

    public static final String COMMON_INTERCEPTOR_PATH =
        "/transactions/{transactionId}/persons-with-significant-control-extensions";
    public static final String EXTENSIONS_COUNT_PATH =
        "/persons-with-significant-control-extensions/{pscNotificationId}/extensionCount";
    public static final String FILINGS_RESOURCE_PATH =
        "/private/transactions/{transactionId}/persons-with-significant-control-extensions/{filingResourceId}/filings";
    public static final String VALIDATION_STATUS_PATH =
        "/persons-with-significant-control-extensions/{pscNotificationId}/{companyNumber}/validation_status";

    @Override
    public void addInterceptors(@NonNull final InterceptorRegistry registry) {


        registry.addInterceptor(transactionInterceptor())
            .excludePathPatterns(EXTENSIONS_COUNT_PATH)
            .excludePathPatterns(VALIDATION_STATUS_PATH)
            .order(1);

        registry.addInterceptor(openTransactionInterceptor())
            .addPathPatterns(FILINGS_RESOURCE_PATH)
            .addPathPatterns(COMMON_INTERCEPTOR_PATH)
            .order(2);

        registry.addInterceptor(transactionClosedInterceptor())
                .addPathPatterns(FILINGS_RESOURCE_PATH)
                .order(3);

        registry.addInterceptor(requestLoggingInterceptor())
                .order(4);

        registry.addInterceptor(new InternalUserInterceptor())
                .addPathPatterns(FILINGS_RESOURCE_PATH)
                .order(5);
    }

    @Bean("chsTransactionInterceptor")
    public TransactionInterceptor transactionInterceptor() {
        return new TransactionInterceptor(APPLICATION_NAMESPACE);
    }

    @Bean("chsOpenTransactionInterceptor")
    public OpenTransactionInterceptor openTransactionInterceptor() {
        return new OpenTransactionInterceptor(APPLICATION_NAMESPACE);
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

