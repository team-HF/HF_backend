package com.hf.healthfriend.domain.chat.repository;

import com.hf.healthfriend.domain.chat.entity.chatmessage.ChatMessage;
import com.hf.healthfriend.domain.chat.repository.custom.ChatMessageCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageCustomRepository {
}
