package com.hf.healthfriend.domain.chat.entity.chatmessage;

import com.hf.healthfriend.domain.chat.entity.ChatParticipation;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("IMAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ImageChatMessage extends ChatMessage {
    private String imageUrl;

    public ImageChatMessage(Long chatMessageId) {
        super(chatMessageId);
    }

    public ImageChatMessage(ChatParticipation chatParticipation, String imageUrl) {
        super(chatParticipation);
        this.imageUrl = imageUrl;
    }
}
