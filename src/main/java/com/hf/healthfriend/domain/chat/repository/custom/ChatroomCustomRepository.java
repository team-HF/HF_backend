package com.hf.healthfriend.domain.chat.repository.custom;

import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.member.entity.Member;

public interface ChatroomCustomRepository {

    Chatroom saveWithParticipants(Member... participants);
}
