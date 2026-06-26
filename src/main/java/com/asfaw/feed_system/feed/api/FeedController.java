package com.asfaw.feed_system.feed.api;

import com.asfaw.feed_system.common.api.PageResponse;
import com.asfaw.feed_system.feed.FeedService;
import com.asfaw.feed_system.post.api.dto.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@Tag(name = "Feed", description = "Personalized news feed endpoints")
public class FeedController {

	private final FeedService feedService;

	@GetMapping
	@Operation(summary = "Get personalized feed", description = "Retrieves personalized news feed for the authenticated user. Uses Redis cache when available, falls back to database query.")
	@ApiResponse(responseCode = "200", description = "Feed retrieved successfully")
	@ApiResponse(responseCode = "401", description = "Unauthorized")
	@ApiResponse(responseCode = "429", description = "Rate limit exceeded")
	public PageResponse<PostResponse> getPersonalizedFeed(
			@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
		return feedService.getPersonalizedFeed(page, size);
	}
}
