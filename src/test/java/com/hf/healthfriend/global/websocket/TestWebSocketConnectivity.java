package com.hf.healthfriend.global.websocket;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThatNoException;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestWebSocketConnectivity {

    @LocalServerPort
    int serverPort;

    @DisplayName("웹 소켓에 제대로 연결이 되는지 테스트 - SockJs 클라이언트 사용")
    @Test
    void testConnectivity() throws ExecutionException, InterruptedException {
        WebSocketClient webSocketClient = new SockJsClient(List.of(
                new WebSocketTransport(new StandardWebSocketClient()),
                new RestTemplateXhrTransport(new RestTemplate())
        ));
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        String url = "ws://localhost:" + this.serverPort + "/hf";
        StompSessionHandler stompSessionHandler = new MyStompSessionHandler();

        assertThatNoException()
                .isThrownBy(() -> stompClient.connectAsync(url, stompSessionHandler).get());
    }

    @DisplayName("웹 소켓에 제대로 연결이 되는지 테스트 - SockJs 클라이언트 사용 - RestTemplate만 사용")
    @Test
    void testConnectivity_sockJS_restTemplate() throws ExecutionException, InterruptedException {
        WebSocketClient webSocketClient = new SockJsClient(List.of(
                new RestTemplateXhrTransport(new RestTemplate())
        ));
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        String url = "ws://localhost:" + this.serverPort + "/hf";
        StompSessionHandler stompSessionHandler = new MyStompSessionHandler();

        assertThatNoException()
                .isThrownBy(() -> stompClient.connectAsync(url, stompSessionHandler).get());
    }

    static class MyStompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            log.info("sessionId={}", session.getSessionId());
            log.info("connectedHeaders={}", connectedHeaders);
        }
    }
}
