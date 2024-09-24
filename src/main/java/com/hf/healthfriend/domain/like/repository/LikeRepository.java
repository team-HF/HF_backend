package com.hf.healthfriend.domain.like.repository;

import com.hf.healthfriend.domain.like.entity.Like;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
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
            SELECT l FROM Like l
            WHERE l.member.id = :memberId
                AND l.canceled = FALSE
            """)
    List<Like> findByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT l FROM Like l
            WHERE l.post.postId = :postId
                AND l.canceled = FALSE
            """)
    List<Like> findByPostId(@Param("postId") Long postId);

    @Query("""
            SELECT COUNT(l.likeId)
            FROM Like l
            WHERE l.post.postId = :postId
                AND l.canceled = FALSE
            """)
    Long countByPostId(@Param("postId") Long postId);

    boolean existsByPostAndMember(Post post, Member member);
}
