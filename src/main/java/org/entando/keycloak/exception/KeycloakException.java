package org.entando.keycloak.exception;

public class KeycloakException extends RuntimeException {

    public KeycloakException() {
        super();
    }

    public KeycloakException(final String message) {
        super(message);
    }

}
