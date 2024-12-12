package com.hf.healthfriend.domain.chat.entity.chatmessage;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TEXT")
public class TextChatMessage extends ChatMessage {
    private String text;
}
