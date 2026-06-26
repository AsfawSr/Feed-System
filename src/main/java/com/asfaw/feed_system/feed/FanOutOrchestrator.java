package com.asfaw.feed_system.feed;

import com.asfaw.feed_system.follow.FollowRepository;
import com.asfaw.feed_system.post.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FanOutOrchestrator {

	private final FollowRepository followRepository;
	private final FeedCacheService feedCacheService;

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

		for (Long followerId : followerIds) {
			feedCacheService.addToUserFeed(followerId, post.getId(), post.getCreatedAt());
		}

		log.debug("Fan-out to {} followers completed", followerIds.size());
	}

	private void fanOutToRecentFollowers(Post post, Long authorId) {
		// For hot users, only fan out to recent followers (e.g., last 1000)
		// This is a simplified strategy - in production, you might use a more sophisticated approach
		List<Long> recentFollowerIds = followRepository.findRecentFollowerIdsByFollowingId(
				authorId, PageRequest.of(0, 1000));

		for (Long followerId : recentFollowerIds) {
			feedCacheService.addToUserFeed(followerId, post.getId(), post.getCreatedAt());
		}

		log.debug("Partial fan-out to {} recent followers completed", recentFollowerIds.size());
	}
}
