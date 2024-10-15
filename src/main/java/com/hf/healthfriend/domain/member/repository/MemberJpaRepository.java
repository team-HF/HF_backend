package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByLoginId(String loginId);

    boolean existsByEmail(String email);

    boolean existsByLoginId(String loginId);

    @Transactional
    @Modifying // 조회가 아닌 변경성 작업에는 해당 어노테이션을 붙여줘야 함
    @Query(value = "UPDATE Members m SET m.review_score=:reviewScore WHERE m.member_id=:memberId ", nativeQuery = true)
    void updateMemberReviewScore(@Param("memberId")long memberId, @Param("reviewScore")double reviewScore);


}
