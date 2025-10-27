package uk.gov.companieshouse.psc.extensions.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.*;
import uk.gov.companieshouse.psc.extensions.api.interceptor.LoggingInterceptor;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InterceptorConfigTest {
    private InterceptorConfig testConfig;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private InterceptorRegistry interceptorRegistry;


    @BeforeEach
    void setUp() {
        testConfig = new InterceptorConfig();
        testConfig.addInterceptors(interceptorRegistry);
    }

    @Test
    void addInterceptorsInvocations() {
        verify(interceptorRegistry, times(1)).addInterceptor(any(TransactionInterceptor.class));
        verify(interceptorRegistry, times(1)).addInterceptor(any(OpenTransactionInterceptor.class));
        verify(interceptorRegistry, times(1)).addInterceptor(any(MappablePermissionsInterceptor.class));
        verify(interceptorRegistry, times(1)).addInterceptor(any(ClosedTransactionInterceptor.class));
        verify(interceptorRegistry, times(1)).addInterceptor(any(LoggingInterceptor.class));
        verify(interceptorRegistry, times(1)).addInterceptor(any(InternalUserInterceptor.class));
    }

    @Test
    void addInterceptors() {
        verify(interceptorRegistry.addInterceptor(any(MappablePermissionsInterceptor.class))).order(3);
        verify(interceptorRegistry.addInterceptor(any(LoggingInterceptor.class))).order(5);
    }

    @Test
    void testTransactionInterceptor() {
        assertThat(testConfig.transactionInterceptor(), isA(TransactionInterceptor.class));
    }

    @Test
    void openTransactionInterceptor() {
        assertThat(testConfig.openTransactionInterceptor(), isA(OpenTransactionInterceptor.class));
    }

    @Test
    void closedTransactionInterceptor() {
        assertThat(testConfig.transactionClosedInterceptor(), isA(ClosedTransactionInterceptor.class));
    }

    @Test
    void requestLoggingInterceptor() {
        assertThat(testConfig.requestLoggingInterceptor(), isA(LoggingInterceptor.class));
    }
}
