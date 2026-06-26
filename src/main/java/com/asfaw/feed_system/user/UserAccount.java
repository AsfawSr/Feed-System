package com.asfaw.feed_system.user;

import com.asfaw.feed_system.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(
		name = "app_users",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_app_users_username", columnNames = "username"),
				@UniqueConstraint(name = "uk_app_users_email", columnNames = "email")
		},
		indexes = {
				@Index(name = "idx_app_users_username", columnList = "username"),
				@Index(name = "idx_app_users_email", columnList = "email"),
				@Index(name = "idx_app_users_created_at", columnList = "created_at")
		}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAccount extends BaseEntity {

	@Column(nullable = false, length = 50)
	private String username;

	@Column(nullable = false, length = 255)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "display_name", nullable = false, length = 100)
	private String displayName;

	@Column(length = 280)
	private String bio;

	@Builder.Default
	@Column(nullable = false)
	private boolean enabled = true;

	@Builder.Default
	@Column(name = "follower_count", nullable = false)
	private long followerCount = 0;

	@Builder.Default
	@Column(name = "following_count", nullable = false)
	private long followingCount = 0;

	@Builder.Default
	@Column(name = "post_count", nullable = false)
	private long postCount = 0;

	public void incrementFollowerCount() {
		followerCount++;
	}

	public void decrementFollowerCount() {
		if (followerCount > 0) {
			followerCount--;
		}
	}

	public void incrementFollowingCount() {
		followingCount++;
	}

	public void decrementFollowingCount() {
		if (followingCount > 0) {
			followingCount--;
		}
	}

	public void incrementPostCount() {
		postCount++;
	}
}
