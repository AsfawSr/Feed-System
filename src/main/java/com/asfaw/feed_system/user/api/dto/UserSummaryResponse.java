package com.asfaw.feed_system.user.api.dto;

public record UserSummaryResponse(
		Long id,
		String username,
		String displayName,
		String bio,
		long followerCount,
		long followingCount,
		long postCount
) {
}
