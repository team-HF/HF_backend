package com.hf.healthfriend.domain.matching.repository;

import com.hf.healthfriend.domain.matching.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
}