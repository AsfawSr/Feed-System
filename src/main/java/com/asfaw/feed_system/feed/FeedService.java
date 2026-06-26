package com.asfaw.feed_system.feed;

import com.asfaw.feed_system.auth.security.AuthenticatedUser;
import com.asfaw.feed_system.common.api.PageResponse;
import com.asfaw.feed_system.common.rate.RateLimitExceededException;
import com.asfaw.feed_system.common.rate.RateLimitService;
import com.asfaw.feed_system.follow.FollowRepository;
import com.asfaw.feed_system.post.Post;
import com.asfaw.feed_system.post.PostMapper;
import com.asfaw.feed_system.post.PostRepository;
import com.asfaw.feed_system.post.api.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

	private final FeedCacheService feedCacheService;
	private final PostRepository postRepository;
	private final PostMapper postMapper;
	private final FollowRepository followRepository;
	private final RateLimitService rateLimitService;

	@Value("${feedsystem.feed.default-page-size:20}")
	private int defaultPageSize;

	@Value("${feedsystem.feed.max-page-size:100}")
	private int maxPageSize;

	public PageResponse<PostResponse> getPersonalizedFeed(int page, int size) {
		AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// Rate limit check
		if (!rateLimitService.allowFeedRead(currentUser.id())) {
			throw new RateLimitExceededException("Feed read rate limit exceeded. Please try again later.");
		}

		// Validate and adjust page size
		size = Math.min(Math.max(size, 1), maxPageSize);

		// Try Redis first
		if (feedCacheService.isFeedCached(currentUser.id())) {
			return getFeedFromRedis(currentUser.id(), page, size);
		}

		// Fallback to database
		log.info("Feed not cached for user {}, falling back to database", currentUser.id());
		return getFeedFromDatabase(currentUser.id(), page, size);
	}

	private PageResponse<PostResponse> getFeedFromRedis(Long userId, int page, int size) {
		long offset = (long) page * size;
		Set<Object> postIds = feedCacheService.getPostIdsFromFeed(userId, offset, size);

		if (postIds == null || postIds.isEmpty()) {
			log.debug("No posts found in Redis feed for user {}", userId);
			return new PageResponse<>(List.of(), page, size, 0, 0, true);
		}

		List<Long> postIdList = postIds.stream()
				.map(obj -> Long.parseLong(obj.toString()))
				.collect(Collectors.toList());

		List<Post> posts = postRepository.findAllById(postIdList);
		List<PostResponse> postResponses = posts.stream()
				.map(postMapper::toResponse)
				.collect(Collectors.toList());

		// Sort by timestamp to maintain order from Redis
		postResponses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		long totalElements = feedCacheService.getFeedSize(userId);
		int totalPages = (int) Math.ceil((double) totalElements / size);
		boolean isLast = page >= totalPages - 1;

		return new PageResponse<>(postResponses, page, size, totalElements, totalPages, isLast);
	}

	private PageResponse<PostResponse> getFeedFromDatabase(Long userId, int page, int size) {
		// Get list of users that current user follows
		List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);

		if (followingIds.isEmpty()) {
			log.debug("User {} is not following anyone", userId);
			return new PageResponse<>(List.of(), page, size, 0, 0, true);
		}

		// Fetch posts from followed users
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
		Page<Post> postsPage = postRepository.findByAuthorIdInOrderByCreatedAtDescIdDesc(followingIds, pageable);

		return PageResponse.from(postsPage.map(postMapper::toResponse));
	}
}
