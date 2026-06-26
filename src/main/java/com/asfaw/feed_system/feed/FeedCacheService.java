package com.asfaw.feed_system.feed;

import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCacheService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${feedsystem.feed.redis-feed-size:1000}")
	private long maxFeedSize;

	private static final String FEED_KEY_PREFIX = "feed:user:";

	public String getFeedKey(Long userId) {
		return FEED_KEY_PREFIX + userId;
	}

	public void addToUserFeed(Long userId, Long postId, Instant timestamp) {
		String feedKey = getFeedKey(userId);
		double score = timestamp.toEpochMilli();

		redisTemplate.opsForZSet().add(feedKey, postId.toString(), score);

		// Trim the feed to max size to prevent unbounded growth
		redisTemplate.opsForZSet().removeRange(feedKey, 0, -maxFeedSize - 1);

		log.debug("Added post {} to user {} feed at {}", postId, userId, timestamp);
	}

	public void removeFromUserFeed(Long userId, Long postId) {
		String feedKey = getFeedKey(userId);
		redisTemplate.opsForZSet().remove(feedKey, postId.toString());
		log.debug("Removed post {} from user {} feed", postId, userId);
	}

	public Set<Object> getPostIdsFromFeed(Long userId, long offset, long count) {
		String feedKey = getFeedKey(userId);
		// Get in reverse order (newest first)
		return redisTemplate.opsForZSet().reverseRange(feedKey, offset, offset + count - 1);
	}

	public long getFeedSize(Long userId) {
		String feedKey = getFeedKey(userId);
		Long size = redisTemplate.opsForZSet().size(feedKey);
		return size != null ? size : 0;
	}

	public void clearUserFeed(Long userId) {
		String feedKey = getFeedKey(userId);
		redisTemplate.delete(feedKey);
		log.debug("Cleared feed for user {}", userId);
	}

	public boolean isFeedCached(Long userId) {
		String feedKey = getFeedKey(userId);
		Boolean hasKey = redisTemplate.hasKey(feedKey);
		return hasKey != null && hasKey;
	}
}
