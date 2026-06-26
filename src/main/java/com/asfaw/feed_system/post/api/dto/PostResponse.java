package com.asfaw.feed_system.post.api.dto;

import com.asfaw.feed_system.user.api.dto.UserSummaryResponse;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

	private Long id;
	private String content;
	private Instant createdAt;
	private UserSummaryResponse author;
}
