package com.asfaw.feed_system.follow.api;

import com.asfaw.feed_system.auth.security.AuthenticatedUser;
import com.asfaw.feed_system.common.api.PageResponse;
import com.asfaw.feed_system.follow.service.FollowService;
import com.asfaw.feed_system.user.api.dto.UserSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Follow", description = "User follow/unfollow and social graph endpoints")
public class FollowController {

	private final FollowService followService;

	@PostMapping("/{userId}/follow")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Follow a user", description = "Follows a specific user. Posts from the followed user will appear in the follower's feed.")
	@ApiResponse(responseCode = "201", description = "Follow successful")
	@ApiResponse(responseCode = "400", description = "Cannot follow self or already following")
	@ApiResponse(responseCode = "401", description = "Unauthorized")
	@ApiResponse(responseCode = "404", description = "User not found")
	public void follow(
			@AuthenticationPrincipal AuthenticatedUser currentUser,
			@Parameter(description = "User ID to follow") @PathVariable Long userId
	) {
		followService.follow(currentUser.id(), userId);
	}

	@DeleteMapping("/{userId}/follow")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Unfollow a user", description = "Unfollows a specific user. Posts from this user will no longer appear in the follower's feed.")
	@ApiResponse(responseCode = "204", description = "Unfollow successful")
	@ApiResponse(responseCode = "401", description = "Unauthorized")
	@ApiResponse(responseCode = "404", description = "User not found")
	public void unfollow(
			@AuthenticationPrincipal AuthenticatedUser currentUser,
			@Parameter(description = "User ID to unfollow") @PathVariable Long userId
	) {
		followService.unfollow(currentUser.id(), userId);
	}

	@GetMapping("/{userId}/followers")
	@Operation(summary = "Get followers", description = "Retrieves paginated list of users who follow the specified user")
	@ApiResponse(responseCode = "200", description = "Followers retrieved successfully")
	public PageResponse<UserSummaryResponse> followers(
			@Parameter(description = "User ID") @PathVariable Long userId,
			@PageableDefault(size = 20) Pageable pageable
	) {
		return PageResponse.from(followService.followers(userId, pageable));
	}

	@GetMapping("/{userId}/following")
	@Operation(summary = "Get following", description = "Retrieves paginated list of users that the specified user follows")
	@ApiResponse(responseCode = "200", description = "Following retrieved successfully")
	public PageResponse<UserSummaryResponse> following(
			@Parameter(description = "User ID") @PathVariable Long userId,
			@PageableDefault(size = 20) Pageable pageable
	) {
		return PageResponse.from(followService.following(userId, pageable));
	}
}
