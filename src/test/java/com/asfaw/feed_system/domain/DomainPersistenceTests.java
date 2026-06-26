package com.asfaw.feed_system.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.asfaw.feed_system.follow.Follow;
import com.asfaw.feed_system.follow.FollowRepository;
import com.asfaw.feed_system.post.Post;
import com.asfaw.feed_system.post.PostRepository;
import com.asfaw.feed_system.user.UserAccount;
import com.asfaw.feed_system.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class DomainPersistenceTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private PostRepository postRepository;

	@Test
	void userRepositoryFindsUsersByUsernameAndEmail() {
		UserAccount user = userRepository.save(user("alice"));

		assertThat(user.getId()).isNotNull();
		assertThat(user.getCreatedAt()).isNotNull();
		assertThat(user.getUpdatedAt()).isNotNull();
		assertThat(userRepository.findByUsername("alice")).contains(user);
		assertThat(userRepository.findByEmail("alice@example.com")).contains(user);
		assertThat(userRepository.existsByUsername("alice")).isTrue();
		assertThat(userRepository.existsByEmail("alice@example.com")).isTrue();
	}

	@Test
	void followRepositoryTracksFollowersAndFollowing() {
		UserAccount alice = userRepository.save(user("alice"));
		UserAccount bob = userRepository.save(user("bob"));
		UserAccount carol = userRepository.save(user("carol"));

		followRepository.save(follow(bob, alice));
		followRepository.save(follow(carol, alice));
		followRepository.save(follow(alice, bob));

		assertThat(followRepository.existsByFollowerIdAndFollowingId(bob.getId(), alice.getId())).isTrue();
		assertThat(followRepository.countByFollowingId(alice.getId())).isEqualTo(2);
		assertThat(followRepository.countByFollowerId(alice.getId())).isEqualTo(1);
		assertThat(followRepository.findByFollowingIdOrderByCreatedAtDescIdDesc(alice.getId(), PageRequest.of(0, 10)))
				.extracting(follow -> follow.getFollower().getUsername())
				.containsExactly("carol", "bob");
	}

	@Test
	void postRepositoryReturnsAuthorTimelineNewestFirst() {
		UserAccount alice = userRepository.save(user("alice"));
		UserAccount bob = userRepository.save(user("bob"));

		postRepository.save(post(alice, "first"));
		Post second = postRepository.save(post(alice, "second"));
		Post bobPost = postRepository.save(post(bob, "bob post"));

		assertThat(postRepository.findByAuthorIdOrderByCreatedAtDescIdDesc(alice.getId(), PageRequest.of(0, 10)))
				.extracting(Post::getContent)
				.containsExactly("second", "first");
		assertThat(postRepository.findByAuthorIdInOrderByCreatedAtDescIdDesc(
				java.util.List.of(alice.getId(), bob.getId()), PageRequest.of(0, 2)))
				.extracting(Post::getId)
				.containsExactly(bobPost.getId(), second.getId());
	}

	private UserAccount user(String username) {
		return UserAccount.builder()
				.username(username)
				.email(username + "@example.com")
				.passwordHash("{bcrypt}hash")
				.displayName(username)
				.build();
	}

	private Follow follow(UserAccount follower, UserAccount following) {
		return Follow.builder()
				.follower(follower)
				.following(following)
				.build();
	}

	private Post post(UserAccount author, String content) {
		return Post.builder()
				.author(author)
				.content(content)
				.build();
	}
}
