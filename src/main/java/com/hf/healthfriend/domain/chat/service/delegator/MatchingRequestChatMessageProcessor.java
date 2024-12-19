package com.hf.healthfriend.domain.chat.service.delegator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.domain.chat.dto.request.ChatMessageSendRequestDto;
import com.hf.healthfriend.domain.chat.dto.request.content.MatchingRequestChatMessageSendRequestContent;
import com.hf.healthfriend.domain.chat.dto.response.ChatMessageSendResponseDto;
import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.chat.entity.chatmessage.MatchingRequestChatMessage;
import com.hf.healthfriend.domain.chat.repository.ChatMessageRepository;
import com.hf.healthfriend.domain.matching.dto.request.MatchingRequestDto;
import com.hf.healthfriend.domain.matching.service.MatchingService;
import com.hf.healthfriend.domain.member.entity.Member;

import java.util.Map;

public class MatchingRequestChatMessageProcessor
        extends AbstractChatMessageProcessorDelegator<MatchingRequestChatMessageSendRequestContent> {
    private static final TypeReference<ChatMessageSendRequestDto<MatchingRequestChatMessageSendRequestContent>> DTO_TYPE =
            new TypeReference<>() {
            };

    private final MatchingService matchingService;
    private final ChatMessageRepository chatMessageRepository;

    public MatchingRequestChatMessageProcessor(ObjectMapper objectMapper,
                                               MatchingService matchingService,
                                               ChatMessageRepository chatMessageRepository) {
        super(objectMapper);
        this.matchingService = matchingService;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    protected TypeReference<ChatMessageSendRequestDto<MatchingRequestChatMessageSendRequestContent>> getTypeReference() {
        return DTO_TYPE;
    }

    @Override
    protected ChatMessageSendResponseDto processSendingMessage(Long chatroomId, ChatMessageSendRequestDto<MatchingRequestChatMessageSendRequestContent> dto) {
        MatchingRequestChatMessage chatMessage = new MatchingRequestChatMessage(
                new Chatroom(chatroomId),
                new Member(dto.getSenderId()),
                dto.getContent().getMeetingTime(),
                dto.getContent().getMeetingPlace(),
                dto.getContent().getMeetingPlace()
        );
        MatchingRequestChatMessage saved = this.chatMessageRepository.save(chatMessage);

        Long generatedMatchingId = this.matchingService.requestMatching(MatchingRequestDto.builder()
                .requesterId(dto.getSenderId())
                .targetId(dto.getContent().getMatchingTargetId())
                .meetingPlace(dto.getContent().getMeetingPlace())
                .meetingPlaceAddress(dto.getContent().getMeetingPlaceAddress())
                .meetingTime(dto.getContent().getMeetingTime())
                .build());

        return ChatMessageSendResponseDto.builder()
                .chatMessageId(saved.getChatMessageId())
                .chatroomId(chatroomId)
                .senderId(dto.getSenderId())
                .creationTime(saved.getCreationTime())
                .lastModified(saved.getLastModified())
                .content(Map.of(
                        "matchingId", generatedMatchingId,
                        "meetingTime", dto.getContent().getMeetingTime(),
                        "meetingPlace", dto.getContent().getMeetingPlace(),
                        "meetingPlaceAddress", dto.getContent().getMeetingPlaceAddress()
                ))
                .build();
    }
}
