package com.asfaw.feed_system.auth.api.dto;

public record AuthResponse(
		String accessToken,
		String tokenType,
		long expiresInSeconds,
		AuthenticatedUserResponse user
) {
}
