package com.asfaw.feed_system.post.api;

import com.asfaw.feed_system.common.api.PageResponse;
import com.asfaw.feed_system.post.PostService;
import com.asfaw.feed_system.post.api.dto.CreatePostRequest;
import com.asfaw.feed_system.post.api.dto.PostResponse;
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
public class PostController {

	private final PostService postService;

	@PostMapping
	public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
		return postService.createPost(request);
	}

	@GetMapping("/{postId}")
	public PostResponse getPost(@PathVariable Long postId) {
		return postService.getPostById(postId);
	}

	@GetMapping("/users/{userId}")
	public PageResponse<PostResponse> getUserPosts(
			@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return postService.getUserPosts(userId, page, size);
	}
}
