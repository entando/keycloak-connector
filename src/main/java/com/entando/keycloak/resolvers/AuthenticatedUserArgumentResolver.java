package com.entando.keycloak.resolvers;

import com.entando.keycloak.security.AuthenticatedUser;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Optional.ofNullable;

@Configuration
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver, WebMvcConfigurer {

	@Value("${keycloak.realm}")
	private String realm;

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(this);
	}
	
	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return parameter.getParameterType().isAssignableFrom(AuthenticatedUser.class);
	}
	
	@Override
	public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
								  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
		final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final KeycloakAuthenticationToken authToken = (KeycloakAuthenticationToken) request.getUserPrincipal();
		return ofNullable(authToken)
				.map(at -> at.getAccount().getKeycloakSecurityContext().getToken())
				.map(token -> new AuthenticatedUser(realm, token))
				.orElse(null);
	}

}
