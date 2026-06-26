package com.asfaw.feed_system.post;

import com.asfaw.feed_system.post.api.dto.PostResponse;
import com.asfaw.feed_system.user.api.dto.UserSummaryResponse;
import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PostMapper {

	@Mapping(source = "author", target = "author")
	PostResponse toResponse(Post post);
}
