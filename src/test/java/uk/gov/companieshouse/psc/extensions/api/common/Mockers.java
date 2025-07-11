package uk.gov.companieshouse.psc.extensions.api.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.companieshouse.psc.extensions.api.exceptions.NotFoundRuntimeException;
import uk.gov.companieshouse.psc.extensions.api.service.UsersService;


public class Mockers {

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    private final WebClient webClient;
    private final UsersService usersService;

    public Mockers(final WebClient webClient,  final UsersService usersService ) {
        this.webClient = webClient;
        this.usersService = usersService;
    }

    private void mockWebClientSuccessResponse( final String uri, final Mono<String> jsonResponse ){
        final var requestHeadersUriSpec = Mockito.mock( WebClient.RequestHeadersUriSpec.class );
        final var requestHeadersSpec = Mockito.mock( WebClient.RequestHeadersSpec.class );
        final var responseSpec = Mockito.mock( WebClient.ResponseSpec.class );

        Mockito.doReturn( requestHeadersUriSpec ).when(webClient).get();
        Mockito.doReturn( requestHeadersSpec ).when( requestHeadersUriSpec ).uri( uri );
        Mockito.doReturn( responseSpec ).when( requestHeadersSpec ).retrieve();
        Mockito.doReturn( jsonResponse ).when( responseSpec ).bodyToMono( String.class );
    }

    public void mockWebClientForFetchUserDetails( final String... userIds ) throws JsonProcessingException {
        for ( final String userId: userIds ){
            final var user = testDataManager.fetchUserDtos( userId ).getFirst();
            final var uri = String.format( "/users/%s", userId );
            final var jsonResponse = new ObjectMapper().writeValueAsString( user );
            mockWebClientSuccessResponse( uri, Mono.just( jsonResponse ) );
        }
    }

    private void mockWebClientErrorResponse( final String uri, int responseCode ){
        final var requestHeadersUriSpec = Mockito.mock( WebClient.RequestHeadersUriSpec.class );
        final var requestHeadersSpec = Mockito.mock( WebClient.RequestHeadersSpec.class );
        final var responseSpec = Mockito.mock( WebClient.ResponseSpec.class );

        Mockito.doReturn( requestHeadersUriSpec ).when(webClient).get();
        Mockito.doReturn( requestHeadersSpec ).when( requestHeadersUriSpec ).uri( uri );
        Mockito.doReturn( responseSpec ).when( requestHeadersSpec ).retrieve();
        Mockito.doReturn( Mono.error( new WebClientResponseException( responseCode, "Error", null, null, null ) ) ).when( responseSpec ).bodyToMono( String.class );
    }

    public void mockWebClientForFetchUserDetailsErrorResponse( final String userId, int responseCode ){
        final var uri = String.format( "/users/%s", userId );
        mockWebClientErrorResponse( uri, responseCode );
    }

    public void mockWebClientForFetchUserDetailsNotFound( final String... userIds ){
        for ( final String userId: userIds ){
            mockWebClientForFetchUserDetailsErrorResponse( userId,404 );
        }
    }

    private void mockWebClientJsonParsingError( final String uri ){
        final var requestHeadersUriSpec = Mockito.mock( WebClient.RequestHeadersUriSpec.class );
        final var requestHeadersSpec = Mockito.mock( WebClient.RequestHeadersSpec.class );
        final var responseSpec = Mockito.mock( WebClient.ResponseSpec.class );

        Mockito.doReturn( requestHeadersUriSpec ).when(webClient).get();
        Mockito.doReturn( requestHeadersSpec ).when( requestHeadersUriSpec ).uri( uri );
        Mockito.doReturn( responseSpec ).when( requestHeadersSpec ).retrieve();
        Mockito.doReturn( Mono.just( "}{" ) ).when( responseSpec ).bodyToMono( String.class );
    }

    public void mockWebClientForFetchUserDetailsJsonParsingError( final String userId ){
        final var uri = String.format( "/users/%s", userId );
        mockWebClientJsonParsingError( uri );
    }

    public void mockUsersServiceFetchUserDetails( final String... userIds ){
        for ( final String userId: userIds ){
            final var userDetails = testDataManager.fetchUserDtos( userId ).getFirst();
            Mockito.doReturn( userDetails ).when( usersService ).fetchUserDetails( userId );
        }
    }


    public void mockUsersServiceFetchUserDetailsNotFound( final String... userIds ){
        for ( final String userId: userIds ){
            Mockito.doThrow( new NotFoundRuntimeException( "Not found.", new Exception( "Not found." ) ) ).when( usersService ).fetchUserDetails( userId );
        }
    }

}
