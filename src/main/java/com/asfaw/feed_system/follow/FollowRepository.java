package com.asfaw.feed_system.follow;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

	Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

	Page<Follow> findByFollowerIdOrderByCreatedAtDescIdDesc(Long followerId, Pageable pageable);

	Page<Follow> findByFollowingIdOrderByCreatedAtDescIdDesc(Long followingId, Pageable pageable);

	long countByFollowerId(Long followerId);

	long countByFollowingId(Long followingId);
}
