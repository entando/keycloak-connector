package org.entando.keycloak.testutils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockKeycloakUserSecurityContextFactory.class)
public @interface WithMockKeycloakUser {
    /**
     * Convenience mechanism for specifying the username. The default is "user". If
     * {@link #username()} is specified it will be used instead of {@link #value()}
     * @return
     */
    String value() default "user";

    /**
     * The username to be used. Note that {@link #value()} is a synonym for
     * {@link #username()}, but if {@link #username()} is specified it will take
     * precedence.
     * @return
     */
    String username() default "";

    /**
     * The roles to use.
     */
    String[] roles() default { "USER" };


    /**
     *  The keycloak resource for which the user has the roles
     */
    String resource() default "";
}
