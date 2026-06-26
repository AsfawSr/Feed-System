package com.asfaw.feed_system.auth.api.dto;

public record AuthenticatedUserResponse(
		Long id,
		String username,
		String email,
		String displayName
) {
}
