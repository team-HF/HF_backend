package com.hf.healthfriend.domain.spec.repository;

import com.hf.healthfriend.domain.spec.entity.Spec;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SpecRepository extends JpaRepository<Spec, Long> {

    @Override
    @Query("""
            SELECT s FROM Spec s
            WHERE s.specId = :specId
                AND s.isDeleted = FALSE
            """)
    @NotNull
    Optional<Spec> findById(@Param("specId") @NotNull Long specId);

    @Query("""
            SELECT s
            FROM Spec s
            WHERE s.member.id = :memberId
                AND s.isDeleted = FALSE
            ORDER BY s.startDate DESC, s.endDate DESC
            """)
    List<Spec> findByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT s
            FROM Spec s
            WHERE s.specId IN (:specIds)
                AND s.isDeleted = FALSE
            """)
    List<Spec> findBySpecIdsIn(@Param("specIds") Set<Long> specIds);
}
