package com.hf.healthfriend.domain.chat.repository;

import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.chat.entity.chatmessage.ChatMessage;
import com.hf.healthfriend.domain.chat.entity.chatmessage.ImageChatMessage;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.testutil.MysqlTestcontainerConfig;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import com.hf.healthfriend.testutil.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
@Import({
        TestConfig.class,
        MysqlTestcontainerConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class TestChatMessageRepository {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ChatroomRepository chatroomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChatParticipationRepository chatParticipationRepository;

    @Test
    @DisplayName("save() - 이미지 메시지 저장 성공")
    void saveImageMessage_success() {
        // Given
        Member sender = SampleEntityGenerator.generateSampleMember("sender@gmail.com", "sender");
        Member receiver = SampleEntityGenerator.generateSampleMember("receiver@gmail.com", "receiver");
        this.memberRepository.save(sender);
        this.memberRepository.save(receiver);

        Chatroom dummyChatroom = SampleEntityGenerator.generateSampleChatroom(sender, receiver);
        this.chatroomRepository.save(dummyChatroom);

        // When
        ImageChatMessage message = new ImageChatMessage(dummyChatroom, sender, "http://localhost/image.jpg");
        assertThatNoException().isThrownBy(() -> this.chatMessageRepository.save(message));

        // Then
        Optional<ChatMessage> findMessageOp = this.chatMessageRepository.findById(message.getChatMessageId());
        assertThat(findMessageOp).isNotEmpty();
        ChatMessage chatMessage = findMessageOp.get();
        assertThat(chatMessage instanceof ImageChatMessage).isTrue();
        assertThat(((ImageChatMessage) chatMessage).getImageUrl()).isEqualTo(message.getImageUrl());
    }
}