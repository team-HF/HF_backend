package com.hf.healthfriend.domain.chat.entity;

import com.hf.healthfriend.domain.BaseTimeEntity;
import com.hf.healthfriend.domain.chat.entity.chatmessage.ChatMessage;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatParticipation extends BaseTimeEntity {

    @EmbeddedId
    private ChatParticipationId chatParticipationId;

    @MapsId("chatroomId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "is_notification_active")
    private boolean notificationActive = true;

    @Column(name = "is_discononected")
    private boolean disconnected = false;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatParticipation(ChatParticipationId id) {
        this.chatParticipationId = id;
    }

    public ChatParticipation(Chatroom chatroom, Member member) {
        this.chatroom = chatroom;
        this.member = member;
        this.chatroom.addChatParticipation(this);
        this.member.addChatParticipation(this);
    }
}
