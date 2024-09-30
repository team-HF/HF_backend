package com.hf.healthfriend.domain.post.repository;

import com.hf.healthfriend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByPostIdAndIsDeletedFalse(Long id);
    Page<Post> findAll(Pageable pageable);
    @Query("""
            SELECT p FROM Post p WHERE p.title 
            LIKE %:keyword% OR p.content LIKE %:keyword%
            """)
    Page<Post> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    Long countCommentsByPostId(Long postId);

}
