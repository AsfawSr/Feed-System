package com.asfaw.feed_system.auth.service;

import com.asfaw.feed_system.auth.api.dto.AuthResponse;
import com.asfaw.feed_system.auth.api.dto.AuthenticatedUserResponse;
import com.asfaw.feed_system.auth.api.dto.LoginRequest;
import com.asfaw.feed_system.auth.api.dto.RegisterRequest;
import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String username = request.username().trim().toLowerCase();
		String email = request.email().trim().toLowerCase();

		if (userRepository.existsByUsername(username)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
		}
		if (userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
		}

		UserAccount user = UserAccount.builder()
				.username(username)
				.email(email)
				.passwordHash(passwordEncoder.encode(request.password()))
				.displayName(request.displayName().trim())
				.build();

		return toAuthResponse(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		UserAccount user = userRepository.findByUsername(request.usernameOrEmail().trim().toLowerCase())
				.or(() -> userRepository.findByEmail(request.usernameOrEmail().trim().toLowerCase()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getUsername(), request.password())
		);

		return toAuthResponse(user);
	}

	private AuthResponse toAuthResponse(UserAccount user) {
		String token = jwtService.generateToken(user);
		return new AuthResponse(
				token,
				"Bearer",
				jwtService.accessTokenExpirationSeconds(),
				new AuthenticatedUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getDisplayName())
		);
	}
}
