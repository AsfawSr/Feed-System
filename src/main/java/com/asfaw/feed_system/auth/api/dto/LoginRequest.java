package com.asfaw.feed_system.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@NotBlank
		String usernameOrEmail,

		@NotBlank
		String password
) {
}
