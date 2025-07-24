package uk.gov.companieshouse.psc.extensions.api;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("app")
@SpringBootTest
class PscExtensionsApiApplicationTest {

    @Mock
    private PscExtensionsApiApplication app;

    @Test
    void contextLoads() {
        // This test will fail if the application context cannot start
        assertThat(app, is(notNullValue()));
    }

}
