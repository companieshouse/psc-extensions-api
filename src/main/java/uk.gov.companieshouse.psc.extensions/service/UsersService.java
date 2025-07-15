package uk.gov.companieshouse.psc.extensions.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.companieshouse.psc.extensions.exceptions.InternalServerErrorRuntimeException;
import uk.gov.companieshouse.psc.extensions.exceptions.NotFoundRuntimeException;
import uk.gov.companieshouse.api.accounts.user.model.User;

import java.time.Duration;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.companieshouse.psc.extensions.utils.LoggingUtil.LOGGER;
import static uk.gov.companieshouse.psc.extensions.utils.ParsingUtil.parseJsonTo;
import static uk.gov.companieshouse.psc.extensions.utils.RequestContextUtil.*;

@Service
public class UsersService {

    private final WebClient usersWebClient;

    private UsersService( @Qualifier( "usersWebClient" ) final WebClient usersWebClient ){
        this.usersWebClient = usersWebClient;
    }

    private Mono<User> toFetchUserDetailsRequest( final String userId, final String xRequestId ) {
        return usersWebClient.get()
                .uri( String.format( "/users/%s", userId ) )
                .retrieve()
                .bodyToMono( String.class )
                .map( parseJsonTo( User.class ) )
                .onErrorMap( throwable -> {
                    if ( throwable instanceof WebClientResponseException exception && NOT_FOUND.equals( exception.getStatusCode() ) ){
                        return new NotFoundRuntimeException( "Failed to find user", exception );
                    }
                    throw new InternalServerErrorRuntimeException( "Failed to retrieve user details", (Exception) throwable );
                } )
                .doOnSubscribe( onSubscribe -> LOGGER.infoContext( xRequestId, String.format( "Sending request to accounts-user-api: GET /users/{user_id}. Attempting to retrieve user: %s", userId ), null ) )
                .doFinally( signalType -> LOGGER.infoContext( xRequestId, String.format( "Finished request to accounts-user-api for user: %s", userId ), null ) );
    }

    public User fetchUserDetails( final String userId ){
        return toFetchUserDetailsRequest( userId, getXRequestId() ).block( Duration.ofSeconds( 20L ) );
    }

}
