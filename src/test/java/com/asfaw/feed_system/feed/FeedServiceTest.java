package com.asfaw.feed_system.feed;

import com.asfaw.feed_system.auth.security.AuthenticatedUser;
import com.asfaw.feed_system.common.rate.RateLimitService;
import com.asfaw.feed_system.follow.FollowRepository;
import com.asfaw.feed_system.post.Post;
import com.asfaw.feed_system.post.PostMapper;
import com.asfaw.feed_system.post.PostRepository;
import com.asfaw.feed_system.post.api.dto.PostResponse;
import com.asfaw.feed_system.user.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

	@Mock
	private FeedCacheService feedCacheService;

	@Mock
	private PostRepository postRepository;

	@Mock
	private PostMapper postMapper;

	@Mock
	private FollowRepository followRepository;

	@Mock
	private RateLimitService rateLimitService;

	@InjectMocks
	private FeedService feedService;

	private UserAccount user;
	private Post post;
	private PostResponse postResponse;

	@BeforeEach
	void setUp() {
		user = UserAccount.builder()
				.id(1L)
				.username("testuser")
				.build();
		post = Post.builder()
				.id(1L)
				.author(user)
				.content("Test post")
				.build();
		postResponse = new PostResponse(1L, "Test post", java.time.Instant.now(), null);

		AuthenticatedUser authUser = new AuthenticatedUser(1L, "testuser", "password", true);
		SecurityContextHolder.getContext().setAuthentication(authUser);
	}

	@Test
	void getPersonalizedFeed_RateLimitExceeded_ThrowsException() {
		when(rateLimitService.allowFeedRead(any())).thenReturn(false);

		assertThrows(com.asfaw.feed_system.common.rate.RateLimitExceededException.class,
				() -> feedService.getPersonalizedFeed(0, 20));
	}

	@Test
	void getPersonalizedFeed_RedisCacheHit_ReturnsCachedFeed() {
		when(rateLimitService.allowFeedRead(any())).thenReturn(true);
		when(feedCacheService.isFeedCached(any())).thenReturn(true);
		when(feedCacheService.getPostIdsFromFeed(any(), any(), any()))
				.thenReturn(List.of("1"));
		when(postRepository.findAllById(any())).thenReturn(List.of(post));
		when(postMapper.toResponse(any())).thenReturn(postResponse);
		when(feedCacheService.getFeedSize(any())).thenReturn(1L);

		var result = feedService.getPersonalizedFeed(0, 20);

		assertNotNull(result);
		assertEquals(1, result.content().size());
	}

	@Test
	void getPersonalizedFeed_DatabaseFallback_ReturnsFeedFromDB() {
		when(rateLimitService.allowFeedRead(any())).thenReturn(true);
		when(feedCacheService.isFeedCached(any())).thenReturn(false);
		when(followRepository.findFollowingIdsByFollowerId(any())).thenReturn(List.of(2L));
		when(postRepository.findByAuthorIdInOrderByCreatedAtDescIdDesc(any(), any()))
				.thenReturn(new PageImpl<>(List.of(post)));
		when(postMapper.toResponse(any())).thenReturn(postResponse);

		var result = feedService.getPersonalizedFeed(0, 20);

		assertNotNull(result);
		assertEquals(1, result.content().size());
	}
}
