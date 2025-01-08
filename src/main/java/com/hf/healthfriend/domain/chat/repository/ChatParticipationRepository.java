package com.hf.healthfriend.domain.chat.repository;

import com.hf.healthfriend.domain.chat.entity.ChatParticipation;
import com.hf.healthfriend.domain.chat.entity.ChatParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipationRepository extends JpaRepository<ChatParticipation, ChatParticipationId> {
}
