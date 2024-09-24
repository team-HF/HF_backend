package com.hf.healthfriend.domain.comment.repository;

import com.hf.healthfriend.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.commentId = :commentId
                AND c.isDeleted = FALSE
            """)
    Optional<Comment> findById(@Param("commentId") Long commentId);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.post.postId = :postId
                AND c.isDeleted = FALSE
            """)
    List<Comment> findByPostId(@Param("postId") long postId);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.writer.id = :writerId
                AND c.isDeleted = FALSE
            """)
    List<Comment> findByWriterId(@Param("writerId") long writerId);
}
