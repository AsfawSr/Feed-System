package com.asfaw.feed_system.follow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FollowControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void authenticatedUserCanFollowAndListFollowers() throws Exception {
		RegisteredUser alice = register("follow_alice");
		RegisteredUser bob = register("follow_bob");

		mockMvc.perform(post("/api/users/{userId}/follow", bob.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(alice.token())))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/users/{userId}/followers", bob.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(alice.token())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].username").value("follow_alice"))
				.andExpect(jsonPath("$.content[0].followerCount").value(0));

		mockMvc.perform(get("/api/users/{userId}/following", alice.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(alice.token())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].username").value("follow_bob"));
	}

	@Test
	void authenticatedUserCanUnfollow() throws Exception {
		RegisteredUser alice = register("unfollow_alice");
		RegisteredUser bob = register("unfollow_bob");

		mockMvc.perform(post("/api/users/{userId}/follow", bob.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(alice.token())))
				.andExpect(status().isCreated());

		mockMvc.perform(delete("/api/users/{userId}/follow", bob.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(alice.token())))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/users/{userId}/following", alice.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(alice.token())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	void authenticatedUserCannotFollowSelf() throws Exception {
		RegisteredUser alice = register("self_follow_alice");

		mockMvc.perform(post("/api/users/{userId}/follow", alice.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(alice.token())))
				.andExpect(status().isConflict());
	}

	private RegisteredUser register(String username) throws Exception {
		String response = mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(Map.of(
								"username", username,
								"email", username + "@example.com",
								"password", "password123",
								"displayName", username
						))))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode body = objectMapper.readTree(response);
		return new RegisteredUser(
				body.path("user").path("id").asLong(),
				body.path("accessToken").asText()
		);
	}

	private String bearer(String token) {
		return "Bearer " + token;
	}

	private record RegisteredUser(Long id, String token) {
	}
}
