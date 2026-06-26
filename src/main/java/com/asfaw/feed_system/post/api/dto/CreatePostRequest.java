package com.asfaw.feed_system.post.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

	@NotBlank(message = "Content cannot be blank")
	@Size(max = 280, message = "Content must be at most 280 characters")
	private String content;
}
