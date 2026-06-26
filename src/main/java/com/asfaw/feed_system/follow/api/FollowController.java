package com.asfaw.feed_system.follow.api;

import com.asfaw.feed_system.auth.security.AuthenticatedUser;
import com.asfaw.feed_system.common.api.PageResponse;
import com.asfaw.feed_system.follow.service.FollowService;
import com.asfaw.feed_system.user.api.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FollowController {

	private final FollowService followService;

	@PostMapping("/{userId}/follow")
	@ResponseStatus(HttpStatus.CREATED)
	public void follow(
			@AuthenticationPrincipal AuthenticatedUser currentUser,
			@PathVariable Long userId
	) {
		followService.follow(currentUser.id(), userId);
	}

	@DeleteMapping("/{userId}/follow")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unfollow(
			@AuthenticationPrincipal AuthenticatedUser currentUser,
			@PathVariable Long userId
	) {
		followService.unfollow(currentUser.id(), userId);
	}

	@GetMapping("/{userId}/followers")
	public PageResponse<UserSummaryResponse> followers(
			@PathVariable Long userId,
			@PageableDefault(size = 20) Pageable pageable
	) {
		return PageResponse.from(followService.followers(userId, pageable));
	}

	@GetMapping("/{userId}/following")
	public PageResponse<UserSummaryResponse> following(
			@PathVariable Long userId,
			@PageableDefault(size = 20) Pageable pageable
	) {
		return PageResponse.from(followService.following(userId, pageable));
	}
}
