package com.hf.healthfriend.domain.post.repository;

import com.hf.healthfriend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByPostIdAndIsDeletedFalse(Long id);
    Page<Post> findAll(Pageable pageable);

    Long countCommentsByPostId(Long postId);

}
