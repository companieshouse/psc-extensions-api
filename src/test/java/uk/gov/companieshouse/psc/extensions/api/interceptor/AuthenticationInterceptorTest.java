package uk.gov.companieshouse.psc.extensions.api.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationInterceptorTest {

    private AuthenticationInterceptor authenticationInterceptor;

     @BeforeEach
     void setUp() {
         authenticationInterceptor = new AuthenticationInterceptor();
     }

     @Test
    void preHandleWithoutHeaders_ShouldReturn401() {
         final var request = new MockHttpServletRequest();
         final var response = new MockHttpServletResponse();

         assertFalse(authenticationInterceptor.preHandle(request, response, null));
         assertEquals(401, response.getStatus());
     }

    @Test
    void preHandleWithoutEricIdentity_ShouldReturn401() {
        final var request = new MockHttpServletRequest();
        final var response = new MockHttpServletResponse();
        request.addHeader("Eric-Identity-Type", "oauth2");

        assertFalse( authenticationInterceptor.preHandle(request, response, null ) );
        assertEquals( 401, response.getStatus() );
    }

    @Test
    void preHandleWithoutEricIdentityType_ShouldReturn401() {
        final var request = new MockHttpServletRequest();
        request.addHeader("Eric-Identity", "abcd123456");

        final var response = new MockHttpServletResponse();
        assertFalse(authenticationInterceptor.preHandle( request, response, null ) );
        assertEquals( 401, response.getStatus() );
    }

    @Test
    void preHandleWithIncorrectEricIdentityType_ShouldReturn401() {
        final var request = new MockHttpServletRequest();
        request.addHeader("Eric-Identity", "abcd123456");
        request.addHeader("Eric-Identity-Type", "random");

        final var response = new MockHttpServletResponse();
        assertFalse( authenticationInterceptor.preHandle(request, response, null ) );
        assertEquals( 401, response.getStatus() );
    }

    @Test
    void preHandle_WhenAuthHeaderAndAuthHeaderTypeOauthAreProvided_ShouldReturnTrue() {
        final var request = new MockHttpServletRequest();
        request.addHeader("Eric-identity", "111");
        request.addHeader("Eric-identity-type", "oauth2");

        final var response = new MockHttpServletResponse();
        assertTrue( authenticationInterceptor.preHandle(request, response, null ) );
    }

    @Test
    void preHandle_WhenAuthHeaderAndAuthHeaderTypeKeyAreProvided_ShouldReturnTrue() {
        final var request = new MockHttpServletRequest();
        request.addHeader("Eric-identity", "111");
        request.addHeader("Eric-identity-type", "key");
        request.addHeader("ERIC-Authorised-Key-Roles", "*");

        final var response = new MockHttpServletResponse();
        assertTrue( authenticationInterceptor.preHandle(request, response, null ) );
    }
}
