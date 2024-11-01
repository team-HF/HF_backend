package com.hf.healthfriend.domain.follow.repository;

import com.hf.healthfriend.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
