package org.entando.keycloak.testutils;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WithMockKeycloakUserSecurityContextFactory implements WithSecurityContextFactory<WithMockKeycloakUser> {

    @Value("${keycloak.resource}")
    private String defaultKeycloakResource;

    public SecurityContext createSecurityContext(WithMockKeycloakUser keycloakMockedUser) {
        String username = StringUtils.hasLength(keycloakMockedUser.username()) ? keycloakMockedUser
            .username() : keycloakMockedUser.value();
        if (username == null) {
            throw new IllegalArgumentException(keycloakMockedUser
                + " cannot have null username on both username and value properites");
        }

        String resource = StringUtils.hasLength(keycloakMockedUser.resource()) ? keycloakMockedUser
            .resource() : defaultKeycloakResource;

        Map<String, AccessToken.Access> resourceAccess = new HashMap<>();
        Set<String> userRoles = Arrays.stream(keycloakMockedUser.roles()).collect(Collectors.toSet());

        AccessToken keycloakAccessToken = new AccessToken();

        AccessToken.Access access = new AccessToken.Access();
        userRoles.forEach(access::addRole);
        resourceAccess.put(resource, access);
        keycloakAccessToken.setResourceAccess(resourceAccess);

        RefreshableKeycloakSecurityContext securityContext = mock(RefreshableKeycloakSecurityContext.class);
        when(securityContext.getToken()).thenReturn(keycloakAccessToken);

        Principal principal = new KeycloakPrincipal<>(username, securityContext);
        KeycloakAccount keycloakAccount = new SimpleKeycloakAccount(principal, userRoles, securityContext);
        Authentication authentication = new KeycloakAuthenticationToken(keycloakAccount, false);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
