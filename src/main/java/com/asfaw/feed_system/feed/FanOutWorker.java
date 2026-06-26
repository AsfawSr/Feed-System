package com.asfaw.feed_system.feed;

import com.asfaw.feed_system.feed.config.FanOutAsyncConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FanOutWorker {

	private final FeedCacheService feedCacheService;
	private final FanOutAsyncConfig fanOutAsyncConfig;

	@Async("fanOutExecutor")
	public void processBatch(List<Long> followerIds, Long postId, long timestamp) {
		log.debug("Processing batch of {} followers for post {}", followerIds.size(), postId);

		for (Long followerId : followerIds) {
			try {
				feedCacheService.addToUserFeed(followerId, postId, java.time.Instant.ofEpochMilli(timestamp));
			} catch (Exception e) {
				log.error("Failed to add post {} to user {} feed", postId, followerId, e);
			}
		}

		log.debug("Batch processing completed for post {}", postId);
	}
}
