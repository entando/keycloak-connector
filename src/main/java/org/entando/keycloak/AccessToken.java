package org.entando.keycloak;

import lombok.Data;

import java.time.Instant;

@Data
public class AccessToken {

	private String accessToken;
	private int expiresIn;
	private int refreshExpiresIn;
	private String refreshToken;
	private String tokenType;
	private String sessionState;
	private String scope;
	private long createdTimestamp;
	
	public boolean isExpired() {
		final Instant created = Instant.ofEpochMilli(createdTimestamp);
		final Instant now = Instant.now();
		return now.getEpochSecond() - created.getEpochSecond() > expiresIn;
	}
}
