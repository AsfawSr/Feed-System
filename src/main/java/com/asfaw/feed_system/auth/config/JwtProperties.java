package com.asfaw.feed_system.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feedsystem.jwt")
public record JwtProperties(
		String issuer,
		String secret,
		long accessTokenExpirationMinutes
) {
}
