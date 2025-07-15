package uk.gov.companieshouse.psc.extensions.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.companieshouse.psc.extensions.common.Mockers;
import uk.gov.companieshouse.psc.extensions.common.TestDataManager;
import uk.gov.companieshouse.psc.extensions.exceptions.InternalServerErrorRuntimeException;
import uk.gov.companieshouse.psc.extensions.exceptions.NotFoundRuntimeException;


@ExtendWith( MockitoExtension.class )
@Tag( "unit-test" )
class UsersServiceTest {

    @Mock
    private WebClient usersWebClient;

    @InjectMocks
    private UsersService usersService;

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    private Mockers mockers;

    @BeforeEach
    void setup() {
        mockers = new Mockers( usersWebClient, null );
    }

    @Test
    void fetchUserDetailsForNullOrNonexistentUserReturnsNotFoundRuntimeException() {
        mockers.mockWebClientForFetchUserDetailsErrorResponse( null, 404 );
        Assertions.assertThrows( NotFoundRuntimeException.class, () -> usersService.fetchUserDetails( (String) null ) );

        mockers.mockWebClientForFetchUserDetailsErrorResponse( "404User", 404 );
        Assertions.assertThrows( NotFoundRuntimeException.class, () -> usersService.fetchUserDetails( "404User" ) );
    }

    @Test
    void fetchUserDetailsWithMalformedUserIdReturnsInternalServerErrorRuntimeException() {
        mockers.mockWebClientForFetchUserDetailsErrorResponse( "£$@123", 400 );
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> usersService.fetchUserDetails( "£$@123" ) );
    }

    @Test
    void fetchUserDetailsWithArbitraryErrorReturnsInternalServerErrorRuntimeException() {
        mockers.mockWebClientForFetchUserDetailsJsonParsingError( "MKUser001" );
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> usersService.fetchUserDetails( "MKUser001" ) );
    }

    @Test
    void fetchUserDetailsReturnsSpecifiedUser() throws JsonProcessingException {
        mockers.mockWebClientForFetchUserDetails( "MKUser001" );
        Assertions.assertEquals( "Mario", usersService.fetchUserDetails( "MKUser001" ).getDisplayName() );
    }

}
