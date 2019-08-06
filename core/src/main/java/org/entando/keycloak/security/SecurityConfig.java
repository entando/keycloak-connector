package org.entando.keycloak.security;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationEntryPoint;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@KeycloakConfiguration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Value("${entando.keycloak.sessionStateful:false}")
    private boolean sessionStateful;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        super.configure(http);

        if (!sessionStateful) {
            http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        http.csrf().disable()
                .headers().cacheControl().disable()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/**").permitAll();
    }

    @Override
    protected AuthenticationEntryPoint authenticationEntryPoint() throws Exception {
        return new KeycloakAuthenticationEntryPoint(adapterDeploymentContext()) {
            @Override
            protected void commenceLoginRedirect(final HttpServletRequest request,
                    final HttpServletResponse response) throws IOException {
                if (sessionStateful) {
                    super.commenceLoginRedirect(request, response);
                } else {
                    commenceUnauthorizedResponse(request, response);
                }
            }
        };
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) {
        final KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}
