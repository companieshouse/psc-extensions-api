package uk.gov.companieshouse.psc.extensions.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Objects;

import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    /**
     * Ensure requests are authenticated for a user
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final var hasEricIdentity = Objects.nonNull( request.getHeader( "Eric-Identity" ) );
        final var identityType = request.getHeader( "Eric-Identity-Type" );
        final var hasEricIdentityType = Objects.nonNull( identityType);

        if (!hasEricIdentityType || !hasEricIdentity){
            LOG.debugRequest(request, "AuthenticationInterceptor error: no authorised identity or identity type", null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if (identityType.contains("oauth2")) {
            LOG.debugRequest(request, "authorised as Oauth2 user ", null);
            return true;
        }else if (identityType.contains("key") && AuthorisationUtil.hasInternalUserRole(request)) {
            LOG.debugRequest(request, "authorised as api key (internal user)", null);
            return true;
        }

        LOG.debugRequest(request, "AuthenticationInterceptor error: user not authorised", null);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

}
