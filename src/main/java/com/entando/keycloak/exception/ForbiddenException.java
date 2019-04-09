package com.entando.keycloak.exception;

public class ForbiddenException extends KeycloakException {

    public ForbiddenException(final String message) {
        super(message);
    }

    public ForbiddenException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}