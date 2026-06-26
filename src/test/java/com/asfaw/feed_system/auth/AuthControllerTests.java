package com.asfaw.feed_system.auth;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void registerCreatesUserAndReturnsAccessToken() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(registerBody("alice"))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accessToken", not(blankOrNullString())))
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.user.username").value("alice"))
				.andExpect(jsonPath("$.user.email").value("alice@example.com"));
	}

	@Test
	void registerRejectsDuplicateUsername() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(registerBody("alice"))))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(registerBody("alice"))))
				.andExpect(status().isConflict());
	}

	@Test
	void loginReturnsAccessTokenForValidCredentials() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(registerBody("alice"))))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(Map.of(
								"usernameOrEmail", "alice",
								"password", "password123"
						))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken", not(blankOrNullString())))
				.andExpect(jsonPath("$.user.username").value("alice"));
	}

	private Map<String, String> registerBody(String username) {
		return Map.of(
				"username", username,
				"email", username + "@example.com",
				"password", "password123",
				"displayName", "Alice Example"
		);
	}
}
