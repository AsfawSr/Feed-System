package com.asfaw.feed_system.follow.service;

import com.asfaw.feed_system.follow.Follow;
import com.asfaw.feed_system.follow.FollowRepository;
import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserMapper;
import com.asfaw.feed_system.user.UserRepository;
import com.asfaw.feed_system.user.api.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Transactional
	public void follow(Long followerId, Long followingId) {
		if (followerId.equals(followingId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Users cannot follow themselves");
		}
		if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Already following user");
		}

		UserAccount follower = findUser(followerId);
		UserAccount following = findUser(followingId);

		followRepository.save(Follow.builder()
				.follower(follower)
				.following(following)
				.build());

		follower.incrementFollowingCount();
		following.incrementFollowerCount();
	}

	@Transactional
	public void unfollow(Long followerId, Long followingId) {
		Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
				.orElse(null);
		if (follow == null) {
			return;
		}

		followRepository.delete(follow);
		follow.getFollower().decrementFollowingCount();
		follow.getFollowing().decrementFollowerCount();
	}

	@Transactional(readOnly = true)
	public Page<UserSummaryResponse> followers(Long userId, Pageable pageable) {
		ensureUserExists(userId);
		return followRepository.findByFollowingIdOrderByCreatedAtDescIdDesc(userId, pageable)
				.map(Follow::getFollower)
				.map(userMapper::toSummary);
	}

	@Transactional(readOnly = true)
	public Page<UserSummaryResponse> following(Long userId, Pageable pageable) {
		ensureUserExists(userId);
		return followRepository.findByFollowerIdOrderByCreatedAtDescIdDesc(userId, pageable)
				.map(Follow::getFollowing)
				.map(userMapper::toSummary);
	}

	private void ensureUserExists(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
	}

	private UserAccount findUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}
}
