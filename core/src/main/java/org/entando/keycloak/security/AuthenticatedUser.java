package org.entando.keycloak.security;

import lombok.Getter;
import org.keycloak.representations.AccessToken;

@Getter
public class AuthenticatedUser {

    private final String userId;
    private final String realm;
    private final AccessToken accessToken;

    public AuthenticatedUser(final String realm, final AccessToken accessToken) {
        this.userId = accessToken.getSubject();
        this.realm = realm;
        this.accessToken = accessToken;
    }

}
