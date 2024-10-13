package com.hf.healthfriend.domain.post.repository;

import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.querydsl.PostCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    Optional<Post> findByPostIdAndIsDeletedFalse(Long id);

    Page<Post> findAll(Pageable pageable);

    // 불필요한 필드 로드를 줄이려면 네이티브 쿼리를 적절히 사용할줄 알아야 함
    @Modifying
    @Query(value = "UPDATE post SET likes_count = likes_count + 1 WHERE post_id = :postId", nativeQuery = true)
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query(value = "UPDATE post p SET p.likes_count = p.likes_count - 1 " +
            "WHERE p.post_id = (SELECT l.post_id FROM likes l WHERE l.like_id = :likeId)", nativeQuery = true)
    void decrementLikeCountByLikeId(@Param("likeId") Long likeId);
}
