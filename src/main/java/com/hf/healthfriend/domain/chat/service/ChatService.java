package com.hf.healthfriend.domain.chat.service;

import com.hf.healthfriend.domain.chat.constant.ChatroomListSearchCondition;
import com.hf.healthfriend.domain.chat.constant.ChatDefaultValues;
import com.hf.healthfriend.domain.chat.dto.request.ChatMessageSendRequestDto;
import com.hf.healthfriend.domain.chat.dto.request.ChatParticipationRequestDto;
import com.hf.healthfriend.domain.chat.dto.response.ChatMessageSendResponseDto;
import com.hf.healthfriend.domain.chat.dto.response.ChatParticipationResponseDto;
import com.hf.healthfriend.domain.chat.dto.response.ChatroomListResponseDto;
import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.chat.entity.chatmessage.ChatMessage;
import com.hf.healthfriend.domain.chat.entity.chatmessage.TextChatMessage;
import com.hf.healthfriend.domain.chat.repository.ChatMessageRepository;
import com.hf.healthfriend.domain.chat.repository.ChatParticipationRepository;
import com.hf.healthfriend.domain.chat.repository.ChatroomRepository;
import com.hf.healthfriend.domain.chat.repository.dto.ChatroomListDto;
import com.hf.healthfriend.domain.chat.service.delegator.ChatMessageProcessor;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.global.file.FileUrlResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatroomRepository chatroomRepository;
    private final ChatParticipationRepository chatParticipationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageProcessor chatMessageProcessor;
    private final FileUrlResolver fileUrlResolver;

    /**
     * 채팅 신청. 채팅을 신청할 때, 채팅 신청 회원과 신청을 받는 회원 양쪽을 포함한 채팅방이 생성된다.
     *
     * @param dto 채팅 신청 정보가 담긴 DTO
     * @return 새로 생성된 채팅방과 채팅 참여 정보가 담긴 DTO
     */
    public ChatParticipationResponseDto requestChat(ChatParticipationRequestDto dto) {
        Chatroom newChatroom = this.chatroomRepository.saveWithParticipants(new Member(dto.getRequesterId()),
                new Member(dto.getChatTargetId()));
        return ChatParticipationResponseDto.builder()
                .newChatroomId(newChatroom.getChatroomId())
                .participantIds(List.of(dto.getChatTargetId(), dto.getRequesterId()))
                .creatorId(dto.getRequesterId())
                .build();
    }

    public ChatMessageSendResponseDto sendMessage(Long chatroomId, ChatMessageSendRequestDto<Object> dto) {
        return this.chatMessageProcessor.sendMessage(chatroomId, dto);
    }

    public List<ChatroomListResponseDto> getChatroomList(Long participantId,
                                                   ChatroomListSearchCondition searchCondition,
                                                   Integer page,
                                                   Integer pageSize) {
        page = page == null ? ChatDefaultValues.DEFAULT_PAGE : page;
        pageSize = pageSize == null ? ChatDefaultValues.DEFAULT_PAGE_SIZE : pageSize;

        List<ChatroomListDto> list = this.chatroomRepository.findByParticipantIdAndSearchCondition(
                participantId, searchCondition, PageRequest.of(page - 1, pageSize));

        return list.stream()
                .map((dto) ->
                        ChatroomListResponseDto.builder()
                                .chatroomId(dto.chatroom().getChatroomId())
                                .opponentParticipantId(dto.opponentParticipantId())
                                .opponentParticipantNickname(dto.opponentParticipantNickname())
                                .opponentParticipantProfileImageUrl(this.fileUrlResolver.resolveFileUrl(dto.opponentParticipantProfileImageUrl()))
                                .matchingStatus(dto.matchingStatus())
                                .lastChatMessage(dto.chatroom().getLastChatMessage().getMessageAsText())
                                .chatSentTime(dto.chatroom().getCreationTime()) // lastModified를 반환하는 게 맞을까?
                                .unreadMessageCount(dto.chatroom().getUnreadMessageCount())
                                .build()
                )
                .toList();
    }
}
