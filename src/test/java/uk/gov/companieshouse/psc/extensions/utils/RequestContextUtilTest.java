package uk.gov.companieshouse.psc.extensions.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.gov.companieshouse.psc.extensions.common.TestDataManager;
import uk.gov.companieshouse.psc.extensions.models.context.RequestContext;
import uk.gov.companieshouse.psc.extensions.models.context.RequestContextData.RequestContextDataBuilder;

import static uk.gov.companieshouse.psc.extensions.models.Constants.*;
import static uk.gov.companieshouse.psc.extensions.utils.RequestContextUtil.*;
import static uk.gov.companieshouse.api.util.security.EricConstants.ERIC_IDENTITY;

@ExtendWith( MockitoExtension.class )
@Tag( "unit-test" )
class RequestContextUtilTest {

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    @Test
    void getXRequestIdIsUnknownWhenXRequestIdIsMissing(){
        RequestContext.setRequestContext( new RequestContextDataBuilder().build() );
        Assertions.assertEquals( UNKNOWN, getXRequestId() );
    }

    @Test
    void getXRequestIdRetrievesXRequestId(){
        final var request = new MockHttpServletRequest();
        request.addHeader("X-Request-Id","theId123");
        RequestContext.setRequestContext( new RequestContextDataBuilder().setXRequestId( request ).build() );
        Assertions.assertEquals( "theId123", getXRequestId() );
    }

    @Test
    void getEricIdentityIsUnknownWhenEricIdentityIsMissing(){
        RequestContext.setRequestContext( new RequestContextDataBuilder().build() );
        Assertions.assertEquals( UNKNOWN, getEricIdentity() );
    }

    @Test
    void getEricIdentityRetrievesEricIdentity(){
        final var request = new MockHttpServletRequest();
        request.addHeader( ERIC_IDENTITY,"MKUser001" );
        RequestContext.setRequestContext( new RequestContextDataBuilder().setEricIdentity( request ).build() );
        Assertions.assertEquals( "MKUser001", getEricIdentity() );
    }

    @Test
    void getEricIdentityTypeIsUnknownWhenEricIdentityTypeIsMissing(){
        RequestContext.setRequestContext( new RequestContextDataBuilder().build() );
        Assertions.assertEquals( UNKNOWN, getEricIdentityType() );
    }

    @Test
    void getEricIdentityTypeRetrievesEricIdentityType(){
        final var request = new MockHttpServletRequest();
        request.addHeader("Eric-Identity-Type","oauth2");
        RequestContext.setRequestContext( new RequestContextDataBuilder().setEricIdentityType( request ).build() );
        Assertions.assertEquals( "oauth2", getEricIdentityType() );
    }

    @Test
    void getUserNullWhenUserIsMissing(){
        RequestContext.setRequestContext( new RequestContextDataBuilder().build() );
        Assertions.assertNull( getUser() );
    }

    @Test
    void getUserRetrievesUser(){
        final var user = testDataManager.fetchUserDtos( "MKUser001" ).getFirst();
        RequestContext.setRequestContext( new RequestContextDataBuilder().setUser( user ).build() );
        Assertions.assertEquals( user, getUser() );
    }

}


