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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object Handler) {
        final var hasEricIdentity = Objects.nonNull( request.getHeader( "Eric-Identity" ) );
        final var hasEricIdentityType = Objects.nonNull( request.getHeader( "Eric-Identity-Type" ) );

        if (!hasEricIdentityType || !hasEricIdentity){
            LOG.debugRequest(request, "AuthenticationInterceptor error: no authorised identity or identity type", null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if (isOauth2User(request) || isApiKeyUser(request)) {
            return true;
        }

        LOG.debugRequest(request, "AuthenticationInterceptor error: user not authorised", null);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    private boolean isOauth2User(HttpServletRequest request) {
        return AuthorisationUtil.isOauth2User(request);
    }
    private boolean isApiKeyUser(HttpServletRequest request) {
        return request.getHeader("Eric-Identity-Type").equals("key");
    }
}
