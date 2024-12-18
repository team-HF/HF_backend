package com.hf.healthfriend.domain.chat.service;

import com.hf.healthfriend.domain.chat.dto.request.ChatParticipationRequestDto;
import com.hf.healthfriend.domain.chat.dto.response.ChatParticipationResponseDto;
import com.hf.healthfriend.domain.chat.entity.ChatParticipation;
import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.chat.repository.ChatMessageRepository;
import com.hf.healthfriend.domain.chat.repository.ChatParticipationRepository;
import com.hf.healthfriend.domain.chat.repository.ChatroomRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatroomRepository chatroomRepository;
    private final ChatParticipationRepository chatParticipationRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 채팅 신청. 채팅을 신청할 때, 채팅 신청 회원과 신청을 받는 회원 양쪽을 포함한 채팅방이 생성된다.
     *
     * @param dto 채팅 신청 정보가 담긴 DTO
     * @return 새로 생성된 채팅방과 채팅 참여 정보가 담긴 DTO
     */
    public ChatParticipationResponseDto requestChat(ChatParticipationRequestDto dto) {
        Chatroom chatroom = Chatroom.newChatroom(new Member(dto.getRequesterId()), new Member(dto.getChatTargetId()));
        Chatroom newChatroom = this.chatroomRepository.save(chatroom);
        return ChatParticipationResponseDto.builder()
                .newChatroomId(newChatroom.getChatroomId())
                .participantIds(List.of(dto.getChatTargetId(), dto.getRequesterId()))
                .creatorId(dto.getRequesterId())
                .build();
    }
}
