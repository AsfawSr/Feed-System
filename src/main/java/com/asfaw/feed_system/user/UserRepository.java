package com.asfaw.feed_system.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, Long> {

	Optional<UserAccount> findByUsername(String username);

	Optional<UserAccount> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
