package uk.gov.companieshouse.psc.extensions.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.companieshouse.psc.extensions.models.SpringRole;
import uk.gov.companieshouse.psc.extensions.models.context.RequestContextData;
import uk.gov.companieshouse.psc.extensions.models.context.RequestContextData.RequestContextDataBuilder;

import java.util.List;

import static uk.gov.companieshouse.psc.extensions.models.Constants.*;
import static uk.gov.companieshouse.psc.extensions.models.SpringRole.*;
import static uk.gov.companieshouse.psc.extensions.utils.LoggingUtil.LOGGER;
import static uk.gov.companieshouse.api.util.security.RequestUtils.getRequestHeader;

public class UserAuthenticationFilter extends OncePerRequestFilter {

    private RequestContextData buildRequestContextData( final HttpServletRequest request ){
        return new RequestContextDataBuilder()
                .setXRequestId( request )
                .setEricIdentity( request )
                .setEricIdentityType( request )
                .build();
    }

    private boolean isValidOAuth2Request( final RequestContextData requestContextData ) {
        return !requestContextData.getEricIdentity().equals( UNKNOWN ) && requestContextData.getEricIdentityType().equals( OAUTH2 );
    }

    private SpringRole computeSpringRole( final RequestContextData requestContextData ){
        LOGGER.debugContext( requestContextData.getXRequestId(), "Checking if this is a valid OAuth2 Request...", null );
        return isValidOAuth2Request( requestContextData ) ? BASIC_OAUTH_ROLE : UNKNOWN_ROLE;
    }

    private void setSpringRoles( final RequestContextData requestContextData, final SpringRole springRole ){
        LOGGER.debugContext( requestContextData.getXRequestId(), String.format( "Adding Spring roles: %s", springRole.getValue()), null );
        final var role = new SimpleGrantedAuthority(String.format( "ROLE_%s", springRole.getValue() ));
        SecurityContextHolder.getContext().setAuthentication( new PreAuthenticatedAuthenticationToken( UNKNOWN, UNKNOWN, List.of(role)) );
    }

    @Override
    protected void doFilterInternal( final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain ) {
        try {
            final var requestContextData = buildRequestContextData( request );
            setSpringRoles( requestContextData, computeSpringRole( requestContextData ) );
            filterChain.doFilter( request, response );
        } catch ( Exception exception ) {
            LOGGER.errorContext( getRequestHeader( request, X_REQUEST_ID ), exception, null );
            response.setStatus( 403 );
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

}
