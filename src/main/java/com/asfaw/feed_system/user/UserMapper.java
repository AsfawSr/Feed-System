package com.asfaw.feed_system.user;

import com.asfaw.feed_system.user.api.dto.UserSummaryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserSummaryResponse toSummary(UserAccount user);
}
