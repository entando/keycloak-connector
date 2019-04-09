package com.entando.keycloak.security;

import com.entando.keycloak.exception.ForbiddenException;
import com.entando.keycloak.exception.UnauthorizedException;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
public class AuthenticationInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    private final String clientId;

    @Autowired
    public AuthenticationInterceptor(@Value("${keycloak.resource}") final String clientId) {
        this.clientId = clientId;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }

    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        if (request.getMethod().equals("OPTIONS") || request.getRequestURI().equals("/error")) {
            return true;
        }
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Secured secured = getAnnotation(handlerMethod, Secured.class);

            if (secured != null) {
                final AccessToken accessToken = ofNullable((KeycloakAuthenticationToken) request.getUserPrincipal())
                        .map(at -> at.getAccount().getKeycloakSecurityContext().getToken())
                        .orElseThrow(UnauthorizedException::new);
                checkPermission(accessToken, secured.value());
            }
        }
        return true;
    }

    private <T extends Annotation> T getAnnotation(final HandlerMethod handlerMethod, final Class<T> clazz) {
        return Optional.ofNullable(handlerMethod.getMethod().getAnnotation(clazz))
                .orElse(handlerMethod.getMethod().getDeclaringClass().getAnnotation(clazz));
    }

    private void checkPermission(final AccessToken accessToken, final String[] roles) {
        final AccessToken.Access resourceAccess = accessToken.getResourceAccess(clientId);
        final boolean hasRole = resourceAccess != null && Arrays.stream(roles)
                .map(resourceAccess::isUserInRole)
                .reduce(Boolean::logicalOr)
                .orElse(false);

        if (!hasRole) {
            throw new ForbiddenException("User doesn't have role to execute this request");
        }
    }

}