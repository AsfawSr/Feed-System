package com.asfaw.feed_system.post;

import com.asfaw.feed_system.auth.security.AuthenticatedUser;
import com.asfaw.feed_system.common.api.PageResponse;
import com.asfaw.feed_system.feed.FanOutOrchestrator;
import com.asfaw.feed_system.post.api.dto.CreatePostRequest;
import com.asfaw.feed_system.post.api.dto.PostResponse;
import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostMapper postMapper;
	private final FanOutOrchestrator fanOutOrchestrator;

	@Transactional
	public PostResponse createPost(CreatePostRequest request) {
		AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserAccount author = userRepository.findById(currentUser.id())
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Post post = Post.builder()
				.author(author)
				.content(request.content())
				.build();

		Post savedPost = postRepository.save(post);

		// Trigger fan-out asynchronously
		fanOutPost(savedPost);

		return postMapper.toResponse(savedPost);
	}

	@Async
	protected void fanOutPost(Post post) {
		fanOutOrchestrator.fanOutPost(post);
	}

	@Transactional(readOnly = true)
	public PageResponse<PostResponse> getUserPosts(Long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
		Page<Post> posts = postRepository.findByAuthorIdOrderByCreatedAtDescIdDesc(userId, pageable);
		return PageResponse.from(posts.map(postMapper::toResponse));
	}

	@Transactional(readOnly = true)
	public PostResponse getPostById(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("Post not found"));
		return postMapper.toResponse(post);
	}
}
