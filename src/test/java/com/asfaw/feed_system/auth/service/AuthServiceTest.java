package com.asfaw.feed_system.auth.service;

import com.asfaw.feed_system.auth.api.dto.LoginRequest;
import com.asfaw.feed_system.auth.api.dto.RegisterRequest;
import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private AuthService authService;

	private RegisterRequest registerRequest;
	private LoginRequest loginRequest;
	private UserAccount user;

	@BeforeEach
	void setUp() {
		registerRequest = new RegisterRequest("testuser", "password123", "test@example.com");
		loginRequest = new LoginRequest("testuser", "password123");
		user = UserAccount.builder()
				.id(1L)
				.username("testuser")
				.password("encodedPassword")
				.email("test@example.com")
				.build();
	}

	@Test
	void register_Success() {
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(UserAccount.class))).thenReturn(user);
		when(jwtService.generateToken(any())).thenReturn("jwt-token");

		var response = authService.register(registerRequest);

		assertNotNull(response);
		assertEquals("jwt-token", response.token());
		verify(userRepository).save(any(UserAccount.class));
	}

	@Test
	void register_UsernameExists_ThrowsException() {
		when(userRepository.existsByUsername(anyString())).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
	}

	@Test
	void login_Success() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtService.generateToken(any())).thenReturn("jwt-token");

		var response = authService.login(loginRequest);

		assertNotNull(response);
		assertEquals("jwt-token", response.token());
	}

	@Test
	void login_UserNotFound_ThrowsException() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
	}
}
