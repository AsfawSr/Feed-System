package com.asfaw.feed_system.feed;

import com.asfaw.feed_system.feed.config.FanOutAsyncConfig;
import com.asfaw.feed_system.follow.FollowRepository;
import com.asfaw.feed_system.post.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FanOutOrchestrator {

	private final FollowRepository followRepository;
	private final FeedCacheService feedCacheService;
	private final FanOutWorker fanOutWorker;
	private final FanOutAsyncConfig fanOutAsyncConfig;

	@Value("${feedsystem.fanout.hot-user-follower-threshold:10000}")
	private long hotUserFollowerThreshold;

	public void fanOutPost(Post post) {
		Long authorId = post.getAuthor().getId();
		long followerCount = followRepository.countByFollowingId(authorId);

		log.info("Starting fan-out for post {} by author {} with {} followers",
				post.getId(), authorId, followerCount);

		if (followerCount > hotUserFollowerThreshold) {
			log.warn("Author {} has {} followers (above threshold {}). Using partial fan-out strategy.",
					authorId, followerCount, hotUserFollowerThreshold);
			fanOutToRecentFollowers(post, authorId);
		} else {
			fanOutToAllFollowers(post, authorId);
		}

		log.info("Fan-out completed for post {}", post.getId());
	}

	private void fanOutToAllFollowers(Post post, Long authorId) {
		List<Long> followerIds = followRepository.findFollowerIdsByFollowingId(authorId);
		processInBatches(followerIds, post.getId(), post.getCreatedAt().toEpochMilli());
	}

	private void fanOutToRecentFollowers(Post post, Long authorId) {
		// For hot users, only fan out to recent followers (e.g., last 1000)
		List<Long> recentFollowerIds = followRepository.findRecentFollowerIdsByFollowingId(
				authorId, PageRequest.of(0, 1000));
		processInBatches(recentFollowerIds, post.getId(), post.getCreatedAt().toEpochMilli());
	}

	private void processInBatches(List<Long> followerIds, Long postId, long timestamp) {
		int batchSize = fanOutAsyncConfig.getBatchSize();
		List<List<Long>> batches = partitionList(followerIds, batchSize);

		log.debug("Processing {} followers in {} batches", followerIds.size(), batches.size());

		for (List<Long> batch : batches) {
			fanOutWorker.processBatch(batch, postId, timestamp);
		}
	}

	private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
		List<List<T>> partitions = new ArrayList<>();
		for (int i = 0; i < list.size(); i += batchSize) {
			partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
		}
		return partitions;
	}
}
