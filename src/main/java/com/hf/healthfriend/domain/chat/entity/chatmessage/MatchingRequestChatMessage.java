package com.hf.healthfriend.domain.chat.entity.chatmessage;

import com.hf.healthfriend.domain.chat.entity.ChatParticipation;
import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MAT_REQ")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingRequestChatMessage extends ChatMessage {
    private LocalDateTime meetingDate;
    private String place;

    public MatchingRequestChatMessage(Long chatMessageId) {
        super(chatMessageId);
    }

    public MatchingRequestChatMessage(Chatroom chatroom, Member sender, LocalDateTime meetingDate, String place) {
        super(chatroom, sender);
        this.meetingDate = meetingDate;
        this.place = place;
    }
}
