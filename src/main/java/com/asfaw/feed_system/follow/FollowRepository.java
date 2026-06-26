package com.asfaw.feed_system.follow;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

	Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

	Page<Follow> findByFollowerIdOrderByCreatedAtDescIdDesc(Long followerId, Pageable pageable);

	Page<Follow> findByFollowingIdOrderByCreatedAtDescIdDesc(Long followingId, Pageable pageable);

	long countByFollowerId(Long followerId);

	long countByFollowingId(Long followingId);

	@Query("SELECT f.follower.id FROM Follow f WHERE f.following.id = :followingId")
	List<Long> findFollowerIdsByFollowingId(Long followingId);

	@Query("SELECT f.follower.id FROM Follow f WHERE f.following.id = :followingId ORDER BY f.createdAt DESC")
	List<Long> findRecentFollowerIdsByFollowingId(Long followingId, Pageable pageable);

	@Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :followerId")
	List<Long> findFollowingIdsByFollowerId(Long followerId);
}
