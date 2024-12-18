package com.hf.healthfriend.domain.chat.repository;

import com.hf.healthfriend.domain.chat.entity.ChatParticipation;
import com.hf.healthfriend.domain.chat.entity.ChatParticipationId;
import com.hf.healthfriend.domain.chat.entity.Chatroom;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.testutil.MysqlTestcontainerConfig;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import com.hf.healthfriend.testutil.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        TestConfig.class,
        MysqlTestcontainerConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class TestChatroomRepository {

    @Autowired
    ChatroomRepository chatroomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChatParticipationRepository chatParticipationRepository;

    @Test
    @DisplayName("save() - 채팅방 생성 시 Cascade 설정에 따라 ChatParticipation도 생성")
    void save_checkCascade_success() {
        // Given
        Member requester = SampleEntityGenerator.generateSampleMember("requester@gmail.com", "REQ");
        Member target = SampleEntityGenerator.generateSampleMember("target@gmail.com", "TAR");
        this.memberRepository.save(requester);
        this.memberRepository.save(target);

        // When
        Chatroom chatroom = Chatroom.newChatroom(requester, target);
        this.chatroomRepository.save(chatroom);

        // Then
        Optional<Chatroom> chatroomOp = this.chatroomRepository.findById(chatroom.getChatroomId());
        assertThat(chatroomOp).isNotEmpty();
        List<ChatParticipation> findParticipations = this.chatParticipationRepository.findAll();
        assertThat(findParticipations).size().isEqualTo(2);

        Chatroom findChatroom = chatroomOp.get();

        assertThat(findChatroom.getParticipations().stream().map(ChatParticipation::getChatParticipationId))
                .containsExactlyInAnyOrder(findParticipations.stream().map(ChatParticipation::getChatParticipationId).toArray(ChatParticipationId[]::new));
    }
}