package com.asfaw.feed_system.common.rate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${feedsystem.rate-limit.post-create.capacity:10}")
	private int postCreateCapacity;

	@Value("${feedsystem.rate-limit.post-create.refill-period-seconds:60}")
	private int postCreateRefillPeriodSeconds;

	@Value("${feedsystem.rate-limit.feed-read.capacity:120}")
	private int feedReadCapacity;

	@Value("${feedsystem.rate-limit.feed-read.refill-period-seconds:60}")
	private int feedReadRefillPeriodSeconds;

	private static final String POST_CREATE_PREFIX = "rate_limit:post_create:";
	private static final String FEED_READ_PREFIX = "rate_limit:feed_read:";

	public boolean allowPostCreate(Long userId) {
		String key = POST_CREATE_PREFIX + userId;
		return allowRequest(key, postCreateCapacity, postCreateRefillPeriodSeconds);
	}

	public boolean allowFeedRead(Long userId) {
		String key = FEED_READ_PREFIX + userId;
		return allowRequest(key, feedReadCapacity, feedReadRefillPeriodSeconds);
	}

	private boolean allowRequest(String key, int capacity, int refillPeriodSeconds) {
		long currentTime = System.currentTimeMillis();
		long windowStart = currentTime - (refillPeriodSeconds * 1000L);

		// Remove old entries outside the window
		redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

		// Count current requests in window
		Long currentCount = redisTemplate.opsForZSet().count(key, windowStart, currentTime);
		if (currentCount == null) {
			currentCount = 0L;
		}

		if (currentCount < capacity) {
			// Add current request
			redisTemplate.opsForZSet().add(key, String.valueOf(currentTime), currentTime);
			// Set expiration to clean up old keys
			redisTemplate.expire(key, Duration.ofSeconds(refillPeriodSeconds * 2));
			return true;
		}

		log.debug("Rate limit exceeded for key {}", key);
		return false;
	}

	public long getRemainingRequests(Long userId, String type) {
		String key = type.equals("post_create") ? POST_CREATE_PREFIX + userId : FEED_READ_PREFIX + userId;
		int capacity = type.equals("post_create") ? postCreateCapacity : feedReadCapacity;
		int refillPeriod = type.equals("post_create") ? postCreateRefillPeriodSeconds : feedReadRefillPeriodSeconds;

		long currentTime = System.currentTimeMillis();
		long windowStart = currentTime - (refillPeriod * 1000L);

		redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
		Long currentCount = redisTemplate.opsForZSet().count(key, windowStart, currentTime);

		return capacity - (currentCount != null ? currentCount : 0);
	}
}
