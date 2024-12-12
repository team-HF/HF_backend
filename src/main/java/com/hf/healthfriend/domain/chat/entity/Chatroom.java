package com.hf.healthfriend.domain.chat.entity;

import com.hf.healthfriend.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Chatroom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatroomId;

    @Column(name = "is_deleted")
    private boolean deleted = false;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatParticipation> participations = new ArrayList<>();

    private Long lastChatMessageId;

    public Chatroom(Long chatroomId) {
        this.chatroomId = chatroomId;
    }

    public void addChatParticipation(ChatParticipation chatParticipation) {
        this.participations.add(chatParticipation);
    }
}
