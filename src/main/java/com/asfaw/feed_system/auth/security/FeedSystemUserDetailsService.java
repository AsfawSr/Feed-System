package com.asfaw.feed_system.auth.security;

import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedSystemUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		UserAccount user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new AuthenticatedUser(user.getId(), user.getUsername(), user.getPasswordHash(), user.isEnabled());
	}
}
