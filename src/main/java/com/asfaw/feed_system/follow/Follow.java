package com.asfaw.feed_system.follow;

import com.asfaw.feed_system.common.persistence.BaseEntity;
import com.asfaw.feed_system.user.UserAccount;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
		name = "follows",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_follows_follower_following", columnNames = {"follower_id", "following_id"})
		},
		indexes = {
				@Index(name = "idx_follows_follower_created_at", columnList = "follower_id, created_at"),
				@Index(name = "idx_follows_following_created_at", columnList = "following_id, created_at")
		}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Follow extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "follower_id", nullable = false)
	private UserAccount follower;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "following_id", nullable = false)
	private UserAccount following;
}
