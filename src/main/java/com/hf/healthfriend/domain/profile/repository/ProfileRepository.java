package com.hf.healthfriend.domain.profile.repository;

import com.hf.healthfriend.domain.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
