package com.entando.keycloak.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "false")
public class SecurityDisabledConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().cacheControl().disable()
                .and()
                .headers().xssProtection()
                .and().and()
                .cors().disable()
                .csrf().disable()
                .authorizeRequests().antMatchers("/**").permitAll();
    }
}
