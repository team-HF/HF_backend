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

    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount + 1 WHERE p.postId = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount - 1 WHERE p.postId = " +
            "(SELECT l.post.postId FROM Like l WHERE l.likeId = :likeId)")
    void decrementLikeCountByLikeId(@Param("likeId") Long likeId);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + 1 WHERE p.postId = :postId")
    void incrementCommentsCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount - :decrCount WHERE p.postId = " +
            "(SELECT c.post.postId FROM Comment c WHERE c.commentId = :commentId)")
    void decrementCommentsCountByCommentId(@Param("commentId") Long commentId, @Param("decrCount") int decrCount);
}
