package com.hf.healthfriend.domain.chat.repository.custom;

import com.hf.healthfriend.domain.chat.constant.MatchingResponseType;
import com.hf.healthfriend.domain.chat.dto.request.ChatMessageSendRequestDto;
import com.hf.healthfriend.domain.chat.dto.request.content.ImageChatMessageSendRequestContent;
import com.hf.healthfriend.domain.chat.dto.request.content.MatchingRequestChatMessageSendRequestContent;
import com.hf.healthfriend.domain.chat.dto.request.content.MatchingResponseChatMessageSendRequestContent;
import com.hf.healthfriend.domain.chat.dto.request.content.TextChatMessageSendRequestContent;
import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.chat.entity.chatmessage.*;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageCustomRepositoryImpl implements ChatMessageCustomRepository {
    private final EntityManager em;

    @Override
    public <D extends ChatMessage> D saveMessageWithChatroomId(Long chatroomId,
                                                               ChatMessageSendRequestDto<?> dto) {
        Chatroom chatroomReference = this.em.getReference(Chatroom.class, chatroomId);
        Member senderReference = this.em.getReference(Member.class, dto.getSenderId());
        ChatMessage chatMessage = switch (dto.getChatMessageType()) {
            case TEXT ->
                new TextChatMessage(chatroomReference,
                        senderReference,
                        ((TextChatMessageSendRequestContent)dto.getContent()).getText());
            case IMAGE -> {
                // TODO: 이미지를 어떻게 할 것인가
                throw new UnsupportedOperationException();
            }
            case MATCHING_REQUEST -> {
                MatchingRequestChatMessageSendRequestContent content =
                        (MatchingRequestChatMessageSendRequestContent) dto.getContent();
                yield new MatchingRequestChatMessage(chatroomReference,
                        senderReference,
                        content.getMeetingTime(),
                        content.getMeetingPlace(),
                        content.getMeetingPlaceAddress());
            }
            case MATCHING_RESPONSE -> {
                MatchingResponseChatMessageSendRequestContent content =
                        (MatchingResponseChatMessageSendRequestContent) dto.getContent();
                yield switch (content.getMatchingResponseType()) {
                    case ACCEPTED ->
                            MatchingResponseChatMessage.createAcceptanceMessage(chatroomReference, senderReference);
                    case REJECTED ->
                        MatchingResponseChatMessage.createRejectMessage(chatroomReference,
                                senderReference, content.getCancelMessage());
                };
            }
        };
        this.em.persist(chatMessage);
        return (D) chatMessage;
    }
}
