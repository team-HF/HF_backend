package com.hf.healthfriend.domain.chat.entity.chatmessage;

import com.hf.healthfriend.domain.chat.constant.MatchingResponseType;
import com.hf.healthfriend.domain.chat.entity.ChatParticipation;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MAT_RESP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MatchingResponseChatMessage extends ChatMessage {
    private MatchingResponseType matchingResponseType;
    private String cancelMessage; // 매칭 수락일 경우 null

    public MatchingResponseChatMessage(Long chatMessageId) {
        super(chatMessageId);
    }

    public MatchingResponseChatMessage(ChatParticipation chatParticipation,
                                       MatchingResponseType matchingResponseType) {
        super(chatParticipation);
        this.matchingResponseType = matchingResponseType;
    }

    public MatchingResponseChatMessage(ChatParticipation chatParticipation,
                                       MatchingResponseType matchingResponseType,
                                       String cancelMessage) {
        this(chatParticipation, matchingResponseType);
        this.cancelMessage = cancelMessage;
    }

    public static MatchingResponseChatMessage createAcceptanceMessage(ChatParticipation chatParticipation) {
        return new MatchingResponseChatMessage(chatParticipation, MatchingResponseType.ACCEPTED);
    }

    public static MatchingResponseChatMessage createRejectMessage(ChatParticipation chatParticipation,
                                                                  String cancelMessage) {
        return new MatchingResponseChatMessage(chatParticipation,
                MatchingResponseType.REJECTED,
                cancelMessage);
    }
}
