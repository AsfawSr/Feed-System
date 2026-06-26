package com.asfaw.feed_system.auth.api;

import com.asfaw.feed_system.auth.api.dto.AuthResponse;
import com.asfaw.feed_system.auth.api.dto.LoginRequest;
import com.asfaw.feed_system.auth.api.dto.RegisterRequest;
import com.asfaw.feed_system.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication token")
	@ApiResponse(responseCode = "201", description = "User registered successfully")
	@ApiResponse(responseCode = "400", description = "Invalid input data")
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	@Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
	@ApiResponse(responseCode = "200", description = "Login successful")
	@ApiResponse(responseCode = "401", description = "Invalid credentials")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}
}
