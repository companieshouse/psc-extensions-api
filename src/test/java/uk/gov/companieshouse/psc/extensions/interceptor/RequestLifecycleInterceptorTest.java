package uk.gov.companieshouse.psc.extensions.interceptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.gov.companieshouse.psc.extensions.common.TestDataManager;
import uk.gov.companieshouse.psc.extensions.exceptions.NotFoundRuntimeException;
import uk.gov.companieshouse.psc.extensions.models.context.RequestContext;
import uk.gov.companieshouse.psc.extensions.service.UsersService;

import static uk.gov.companieshouse.psc.extensions.utils.RequestContextUtil.*;

@ExtendWith( MockitoExtension.class )
@Tag( "unit-test" )
class RequestLifecycleInterceptorTest {

    @Mock
    private UsersService usersService;

    @InjectMocks
    private RequestLifecycleInterceptor requestLifecycleInterceptor;

    private final TestDataManager testDataManager = TestDataManager.getInstance();

    @Test
    void preHandleWithOAuth2RequestSetsRequestContextWithUserAndReturnsTrue(){
        final var user = testDataManager.fetchUserDtos( "MKUser001" ).getFirst();

        final var request = new MockHttpServletRequest();
        request.addHeader( "X-Request-Id", "theId123" );
        request.addHeader( "Eric-Identity", user.getUserId() );
        request.addHeader( "Eric-Identity-Type", "oauth2" );

        final var response = new MockHttpServletResponse();

        Mockito.doReturn( user ).when( usersService ).fetchUserDetails( user.getUserId() );

        final var result = requestLifecycleInterceptor.preHandle( request, response, null );

        Assertions.assertTrue( result );
        Assertions.assertEquals( "theId123", getXRequestId() );
        Assertions.assertEquals( user.getUserId(), getEricIdentity() );
        Assertions.assertEquals( "oauth2", getEricIdentityType() );
        Assertions.assertEquals( user, getUser() );
        Assertions.assertEquals( 200, response.getStatus() );
    }

    @Test
    void preHandleWithOAuth2RequestWithNonexistentUserDoesNotSetRequestContextAndReturnsFalse(){
        final var user = testDataManager.fetchUserDtos( "MKUser001" ).getFirst();

        final var request = new MockHttpServletRequest();
        request.addHeader( "X-Request-Id", "theId123" );
        request.addHeader( "Eric-Identity", user.getUserId() );
        request.addHeader( "Eric-Identity-Type", "oauth2" );

        final var response = new MockHttpServletResponse();

        Mockito.doThrow( new NotFoundRuntimeException( "Could not find user", new Exception( "Could not find user" )) ).when( usersService ).fetchUserDetails( user.getUserId() );

        final var result = requestLifecycleInterceptor.preHandle( request, response, null );

        Assertions.assertFalse( result );
        Assertions.assertEquals( "unknown", getXRequestId() );
        Assertions.assertEquals( "unknown", getEricIdentity() );
        Assertions.assertEquals( "unknown", getEricIdentityType() );
        Assertions.assertNull( getUser() );
        Assertions.assertEquals( 403, response.getStatus() );
    }

    @Test
    void afterCompletionClearsRequestContext(){
        final var request = new MockHttpServletRequest();
        request.addHeader( "X-Request-Id", "theId123" );
        request.addHeader( "Eric-Identity", "MKUser001" );
        request.addHeader( "Eric-Identity-Type", "oauth2" );

        final var response = new MockHttpServletResponse();

        requestLifecycleInterceptor.preHandle( request, response, null );

        Assertions.assertEquals( "theId123", getXRequestId() );
        Assertions.assertEquals( "MKUser001", getEricIdentity() );
        Assertions.assertEquals( "oauth2", getEricIdentityType() );

        requestLifecycleInterceptor.afterCompletion( request, response, null, null );

        Assertions.assertEquals( "unknown", getXRequestId() );
        Assertions.assertEquals( "unknown", getEricIdentity() );
        Assertions.assertEquals( "unknown", getEricIdentityType() );
    }

    @AfterEach
    void teardown(){
        RequestContext.clear();
    }

}
