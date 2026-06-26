package com.asfaw.feed_system.feed.api;

import com.asfaw.feed_system.common.api.PageResponse;
import com.asfaw.feed_system.feed.FeedService;
import com.asfaw.feed_system.post.api.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

	private final FeedService feedService;

	@GetMapping
	public PageResponse<PostResponse> getPersonalizedFeed(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return feedService.getPersonalizedFeed(page, size);
	}
}
