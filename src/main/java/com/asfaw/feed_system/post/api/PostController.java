package com.asfaw.feed_system.post.api;

import com.asfaw.feed_system.common.api.PageResponse;
import com.asfaw.feed_system.post.PostService;
import com.asfaw.feed_system.post.api.dto.CreatePostRequest;
import com.asfaw.feed_system.post.api.dto.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Post creation and retrieval endpoints")
public class PostController {

	private final PostService postService;

	@PostMapping
	@Operation(summary = "Create a new post", description = "Creates a new text post and distributes it to followers")
	@ApiResponse(responseCode = "200", description = "Post created successfully")
	@ApiResponse(responseCode = "400", description = "Invalid input or rate limit exceeded")
	@ApiResponse(responseCode = "401", description = "Unauthorized")
	public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
		return postService.createPost(request);
	}

	@GetMapping("/{postId}")
	@Operation(summary = "Get post by ID", description = "Retrieves a specific post by its ID")
	@ApiResponse(responseCode = "200", description = "Post retrieved successfully")
	@ApiResponse(responseCode = "404", description = "Post not found")
	public PostResponse getPost(@Parameter(description = "Post ID") @PathVariable Long postId) {
		return postService.getPostById(postId);
	}

	@GetMapping("/users/{userId}")
	@Operation(summary = "Get user's posts", description = "Retrieves paginated list of posts by a specific user")
	@ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
	public PageResponse<PostResponse> getUserPosts(
			@Parameter(description = "User ID") @PathVariable Long userId,
			@Parameter(description = "Page number (0-based)" )@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
		return postService.getUserPosts(userId, page, size);
	}
}
