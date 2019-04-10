package com.entando.keycloak.exception;

public class ForbiddenException extends KeycloakException {

    public ForbiddenException(final String message) {
        super(message);
    }

}