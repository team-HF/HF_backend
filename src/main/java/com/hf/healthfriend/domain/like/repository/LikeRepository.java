package com.hf.healthfriend.domain.like.repository;

import com.hf.healthfriend.domain.like.entity.Like;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("""
            SELECT l FROM Like l
            WHERE l.likeId = :likeId AND l.canceled = FALSE
            """)
    Optional<Like> findById(@Param("likeId") Long likeId);

    @Query("""
            SELECT l
            FROM Like l
            WHERE l.post.postId = :postId
                AND l.member.id = :memberId
            """)
    Optional<Like> findByMemberIdAndPostId(@Param("memberId") Long memberId,
                                           @Param("postId") Long postId);

    @Query("""
            SELECT l
            FROM Like l
            WHERE l.comment.commentId = :commentId
                AND l.member.id = :memberId
            """)
    Optional<Like> findByMemberIdAndCommentId(@Param("memberId") Long memberId,
                                           @Param("commentId") Long commentId);

    @Query("""
            SELECT COUNT(l.likeId) > 0
            FROM Like l
            WHERE l.member.id = :memberId
                AND l.post.postId = :postId
            """)
    boolean existsByMemberIdAndPostId(@Param("memberId") Long memberId,
                                      @Param("postId") Long postId);

    @Query("""
            SELECT l FROM Like l
            WHERE l.member.id = :memberId
                AND l.likeType = 'POST'
                AND l.canceled = FALSE
            """)
    List<Like> findPostLikeByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT l FROM Like l
            WHERE l.post.postId = :postId
                AND l.canceled = FALSE
            """)
    List<Like> findByPostId(@Param("postId") Long postId);

    @Query("""
            SELECT l FROM Like l
            WHERE l.comment.commentId = :commentId
                AND l.canceled = FALSE
            """)
    List<Like> findByCommentId(@Param("commentId") Long commentId);

    @Query("""
            SELECT COUNT(l.likeId)
            FROM Like l
            WHERE l.post.postId = :postId
                AND l.canceled = FALSE
            """)
    Long countByPostId(@Param("postId") Long postId);

    @Query("""
            SELECT COUNT(l.likeId)
            FROM Like l
            WHERE l.comment.commentId = :commentId
                AND l.canceled = FALSE
            """)
    Long countByCommentId(@Param("commentId") Long commentId);

    boolean existsByPostAndMember(Post post, Member member);

    @Modifying
    @Query(value = "UPDATE likes SET canceled = true WHERE post_id = :postId", nativeQuery = true)
    void deleteLikeByPostId(@Param("postId") Long postId);
}
