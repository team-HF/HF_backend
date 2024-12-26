package com.hf.healthfriend.domain.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.domain.chat.constant.ChatMessageType;
import com.hf.healthfriend.domain.chat.dto.response.ChatMessageSendResponseDto;
import com.hf.healthfriend.domain.chat.dto.response.ChatParticipationResponseDto;
import com.hf.healthfriend.domain.chat.repository.ChatMessageRepository;
import com.hf.healthfriend.domain.chat.repository.ChatParticipationRepository;
import com.hf.healthfriend.domain.chat.repository.ChatroomRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.testutil.MysqlTestcontainerConfig;
import com.hf.healthfriend.testutil.RedisTestConfig;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({MysqlTestcontainerConfig.class, RedisTestConfig.class})
@Slf4j
class TestChatMessagingController {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChatParticipationRepository chatParticipationRepository;

    @Autowired
    ChatroomRepository chatroomRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    PlatformTransactionManager txManager;

    @LocalServerPort
    int port;

    StompSession senderSession;
    StompSession receiverSession;
    Member sender;
    Member receiver;

    @BeforeEach
    void beforeEach() throws ExecutionException, InterruptedException {
        // 채팅에 참여할 두 Member 엔티티 생성 및 데이터베이스에 삽입
        // 여기서 저장된 sender와 receiver 엔티티를 다른 트랜잭션에서 사용해야 하므로
        // 데이터베이스에 커밋 (afterEach에서 삭제)
        TransactionStatus status = this.txManager.getTransaction(new DefaultTransactionAttribute());
        this.sender = SampleEntityGenerator.generateSampleMember("sender@gmail.com", "sender");
        this.receiver = SampleEntityGenerator.generateSampleMember("receiver@gmail.com", "receiver");
        this.memberRepository.save(this.sender);
        this.memberRepository.save(this.receiver);
        this.txManager.commit(status);

        // 웹 소켓 테스트를 위한 Client 생성
        WebSocketStompClient senderClient = generateStompClient();
        WebSocketStompClient receiverClient = generateStompClient();

        // 웹 소켓 handshake
        String websocketUrl = "ws://localhost:" + this.port + "/hf/portfolio";

        // 웹 소켓 통신을 위한 SessionHandler 생성
        StompSessionHandler stompSessionHandler = new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(StompSession session, @NotNull StompHeaders connectedHeaders) {
                log.info("sessionId={}", session.getSessionId());
                log.info("connectedHeaders={}", connectedHeaders);
            }
        };

        // 웹 소켓 통신을 위한 Session 생성
        this.senderSession = senderClient.connectAsync(websocketUrl + "?member-id=" + this.sender.getId(), stompSessionHandler).get();
        this.receiverSession = receiverClient.connectAsync(websocketUrl + "?member-id=" + this.receiver.getId(), stompSessionHandler).get();
    }

    @Autowired
    ObjectMapper objectMapper;

    private WebSocketStompClient generateStompClient() {
        // SockJS 기반 웹 소켓 클라이언트 생성
        WebSocketClient webSocketClient = new SockJsClient(List.of(
                new WebSocketTransport(new StandardWebSocketClient())
        ));
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter(this.objectMapper));
        return stompClient;
    }

    @AfterEach
    void afterEach() {
        // sender와 receiver는 beforeEach에서 별도의 트랜잭션에서 생성되었기 때문에
        // 직접 삭제
        this.memberRepository.delete(this.sender);
        this.memberRepository.delete(this.receiver);
    }

    CountDownLatch latch = new CountDownLatch(3);

    @Test
    @DisplayName("채팅 신청 후 메시지 보내기 테스트 - 성공")
    void chatRequestAndThenSendMessage() throws InterruptedException {
        // 새로운 트랜잭션에서 생성
        TransactionStatus txStatus = this.txManager.getTransaction(new DefaultTransactionAttribute());

        // 비동기 처리 상황에서 결과값을 검증할 객체를 담기 위한 ConcurrentHashMap
        Map<String, Object> resultMap = new ConcurrentHashMap<>();

        this.senderSession.subscribe("/hf/user/" + this.sender.getId() + "/chat/request", new StompFrameHandler() {

            @Override
            public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
                return ChatParticipationResponseDto.class;
            }

            @Override
            public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                log.info("payload={}", payload);
                log.info("payload type: {}", payload.getClass());

                ChatParticipationResponseDto dto = (ChatParticipationResponseDto) payload;
                resultMap.put("ChatParticipationResponseDto", dto);
                messageSendTest(dto, resultMap); // 동기화했기 때문에 이 메소드가 끝날 때까지 대기함
                latch.countDown();
            }
        });

        this.senderSession.send("/hf/app/chat/request", Map.of(
                "requesterId", this.sender.getId(),
                "chatTargetId", this.receiver.getId()
        ));

        boolean await = latch.await(8, TimeUnit.SECONDS);
        log.info("count={}", latch.getCount());

        // Then
        assertThat(await).isTrue();

        // 채팅 신청 검증
        assertThat(this.chatParticipationRepository.findAll()).size().isEqualTo(2);

        ChatParticipationResponseDto chatParticipationResponseDto =
                (ChatParticipationResponseDto) resultMap.get("ChatParticipationResponseDto");
        assertThat(chatParticipationResponseDto.newChatroomId()).isNotNull();
        assertThat(chatParticipationResponseDto.creatorId()).isEqualTo(sender.getId());
        assertThat(chatParticipationResponseDto.participantIds()).containsExactlyInAnyOrder(sender.getId(), receiver.getId());

        // 채팅 메시지 전송 검증
        assertThat(this.chatMessageRepository.findAll()).size().isEqualTo(1);

        Long chatroomId = (Long) resultMap.get("chatroomId");
        String chatMessage = (String) resultMap.get("expectedChatMessage");

        ChatMessageSendResponseDto chatMessageSendResponseToSender =
                (ChatMessageSendResponseDto) resultMap.get(this.sender.getId() + "ChatMessageSendResponseDto");
        assertThat(chatMessageSendResponseToSender.chatMessageId()).isNotNull();
        assertThat(chatMessageSendResponseToSender.chatroomId()).isEqualTo(chatroomId);
        assertThat(chatMessageSendResponseToSender.senderId()).isEqualTo(this.sender.getId());
        assertThat(chatMessageSendResponseToSender.content()).isEqualTo(Map.of("text", chatMessage));

        ChatMessageSendResponseDto chatMessageSendResponseToReceiver =
                (ChatMessageSendResponseDto) resultMap.get(this.receiver.getId() + "ChatMessageSendResponseDto");
        assertThat(chatMessageSendResponseToReceiver.chatMessageId()).isNotNull();
        assertThat(chatMessageSendResponseToReceiver.chatroomId()).isEqualTo(chatroomId);
        assertThat(chatMessageSendResponseToReceiver.senderId()).isEqualTo(this.sender.getId());
        assertThat(chatMessageSendResponseToReceiver.content()).isEqualTo(Map.of("text", chatMessage));

        this.txManager.rollback(txStatus);
    }

    // 위에서는 채팅 신청 테스트, 여기서는 메시지 전송 테스트
    private void messageSendTest(ChatParticipationResponseDto chatParticipationInfo,
                                 Map<String, Object> resultMap) {
        final String chatMessage = "Hello, World!";

        resultMap.put("expectedChatMessage", chatMessage);
        resultMap.put("chatroomId", chatParticipationInfo.newChatroomId());

        Long chatroomId = chatParticipationInfo.newChatroomId();

        subscribeEach(this.senderSession, this.sender, chatroomId, resultMap);
        subscribeEach(this.receiverSession, this.receiver, chatroomId, resultMap);

        this.senderSession.send("/hf/app/chat/messages/" + chatroomId, Map.of(
                "senderId", this.sender.getId(),
                "chatMessageType", ChatMessageType.TEXT,
                "content", Map.of(
                        "text", chatMessage
                )
        ));
    }

    private void subscribeEach(StompSession session,
                               Member member,
                               Long chatroomId,
                               Map<String, Object> resultMap) {
        session.subscribe("/hf/topic/chat/messages/" + chatroomId, new StompFrameHandler() {

            @Override
            public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
                return ChatMessageSendResponseDto.class;
            }

            @Override
            public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                log.info("payload={}", payload);
                log.info("payload type: {}", payload.getClass());
                ChatMessageSendResponseDto result = (ChatMessageSendResponseDto) payload;
                resultMap.put(member.getId() + "ChatMessageSendResponseDto", result);
                latch.countDown();
            }
        });
    }
}