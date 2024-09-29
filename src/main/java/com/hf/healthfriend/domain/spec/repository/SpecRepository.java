package com.hf.healthfriend.domain.spec.repository;

import com.hf.healthfriend.domain.spec.entity.Spec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecRepository extends JpaRepository<Spec, Long> {
}
