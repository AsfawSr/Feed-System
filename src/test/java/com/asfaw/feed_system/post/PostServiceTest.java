package com.asfaw.feed_system.post;

import com.asfaw.feed_system.auth.security.AuthenticatedUser;
import com.asfaw.feed_system.common.rate.RateLimitService;
import com.asfaw.feed_system.feed.FanOutOrchestrator;
import com.asfaw.feed_system.post.api.dto.CreatePostRequest;
import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@Mock
	private PostRepository postRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PostMapper postMapper;

	@Mock
	private FanOutOrchestrator fanOutOrchestrator;

	@Mock
	private RateLimitService rateLimitService;

	@InjectMocks
	private PostService postService;

	private CreatePostRequest createPostRequest;
	private UserAccount user;
	private Post post;

	@BeforeEach
	void setUp() {
		createPostRequest = new CreatePostRequest("Test post content");
		user = UserAccount.builder()
				.id(1L)
				.username("testuser")
				.build();
		post = Post.builder()
				.id(1L)
				.author(user)
				.content("Test post content")
				.build();
	}

	@Test
	void createPost_Success() {
		AuthenticatedUser authUser = new AuthenticatedUser(1L, "testuser", "password", true);
		SecurityContextHolder.getContext().setAuthentication(authUser);

		when(rateLimitService.allowPostCreate(any())).thenReturn(true);
		when(userRepository.findById(any())).thenReturn(Optional.of(user));
		when(postRepository.save(any())).thenReturn(post);
		when(postMapper.toResponse(any())).thenReturn(any());

		postService.createPost(createPostRequest);

		verify(postRepository).save(any(Post.class));
	}

	@Test
	void createPost_RateLimitExceeded_ThrowsException() {
		AuthenticatedUser authUser = new AuthenticatedUser(1L, "testuser", "password", true);
		SecurityContextHolder.getContext().setAuthentication(authUser);

		when(rateLimitService.allowPostCreate(any())).thenReturn(false);

		assertThrows(com.asfaw.feed_system.common.rate.RateLimitExceededException.class,
				() -> postService.createPost(createPostRequest));
	}

	@Test
	void getUserPosts_Success() {
		Pageable pageable = PageRequest.of(0, 20);
		Page<Post> postPage = new PageImpl<>(List.of(post));

		when(postRepository.findByAuthorIdOrderByCreatedAtDescIdDesc(any(), any()))
				.thenReturn(postPage);

		var result = postService.getUserPosts(1L, 0, 20);

		assertNotNull(result);
		assertEquals(1, result.content().size());
	}
}
