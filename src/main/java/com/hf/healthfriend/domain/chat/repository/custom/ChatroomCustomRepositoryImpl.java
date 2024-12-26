package com.hf.healthfriend.domain.chat.repository.custom;

import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

@Repository
@RequiredArgsConstructor
public class ChatroomCustomRepositoryImpl implements ChatroomCustomRepository {
    private final EntityManager em;

    @Override
    public Chatroom saveWithParticipants(Member... participants) {
        Chatroom newChatroom = Chatroom.newChatroom(
                Arrays.stream(participants)
                        .map((e) -> this.em.getReference(Member.class, e.getId()))
                        .toArray(Member[]::new)
        );
        this.em.persist(newChatroom);
        return newChatroom;
    }
}
