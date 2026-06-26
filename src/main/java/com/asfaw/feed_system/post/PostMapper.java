package com.asfaw.feed_system.post;

import com.asfaw.feed_system.post.api.dto.PostResponse;
import com.asfaw.feed_system.user.api.dto.UserSummaryResponse;
import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PostMapper {

	@Mapping(source = "author", target = "author", qualifiedByName = "mapAuthor")
	PostResponse toResponse(Post post);

	@Named("mapAuthor")
	default UserSummaryResponse mapAuthor(UserAccount author) {
		return UserMapper.INSTANCE.toSummary(author);
	}
}
