package com.asfaw.feed_system.post;

import com.asfaw.feed_system.common.persistence.BaseEntity;
import com.asfaw.feed_system.user.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(
		name = "posts",
		indexes = {
				@Index(name = "idx_posts_author_created_at", columnList = "author_id, created_at"),
				@Index(name = "idx_posts_created_at", columnList = "created_at")
		}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "author_id", nullable = false)
	private UserAccount author;

	@Column(nullable = false, length = 280)
	private String content;
}
