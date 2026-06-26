package com.asfaw.feed_system.post;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	Page<Post> findByAuthorIdOrderByCreatedAtDescIdDesc(Long authorId, Pageable pageable);

	List<Post> findByAuthorIdInOrderByCreatedAtDescIdDesc(Collection<Long> authorIds, Pageable pageable);

	Page<Post> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);
}
