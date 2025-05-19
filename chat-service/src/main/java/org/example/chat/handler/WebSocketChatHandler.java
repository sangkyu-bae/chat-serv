package org.example.chat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chat.model.ChatMessage;
import org.example.chat.service.ChatMessageService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatMessageService chatMessageService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 클라이언트로부터 메시지 수신 처리
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> {
                    try {
                        // JSON 메시지를 ChatMessage 객체로 변환
                        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
                        chatMessage.setTimestamp(System.currentTimeMillis());
                        
                        String roomId = chatMessage.getRoomId();
                        String sender = chatMessage.getSender();
                        
                        log.info("WebSocket 메시지 수신: room={}, sender={}, type={}", 
                                roomId, sender, chatMessage.getType());
                        
                        // 메시지 타입에 따른 처리
                        switch (chatMessage.getType()) {
                            case ENTER:
                                // 입장 메시지 처리 및 전송
                                return chatMessageService.sendMessage(chatMessage);
                                
                            case TALK:
                                // 채팅 메시지 전송
                                return chatMessageService.sendMessage(chatMessage);
                                
                            case LEAVE:
                                // 퇴장 메시지 처리 및 전송
                                return chatMessageService.sendMessage(chatMessage);
                                
                            default:
                                return Mono.empty();
                        }
                    } catch (IOException e) {
                        log.error("메시지 처리 중 오류 발생", e);
                        return Mono.empty();
                    }
                })
                .then();
        
        // 세션 URI에서 방 ID 추출 (/ws/chat/{roomId})
        String path = session.getHandshakeInfo().getUri().getPath();
        String[] segments = path.split("/");
        String roomId = segments[segments.length - 1];
        
        log.info("WebSocket 연결 수립: session={}, room={}", session.getId(), roomId);
        
        // 해당 채팅방의 메시지 구독 및 클라이언트로 전송
        Flux<WebSocketMessage> output = chatMessageService.joinRoom(roomId)
                .map(message -> {
                    try {
                        // ChatMessage 객체를 JSON 문자열로 변환
                        String json = objectMapper.writeValueAsString(message);
                        return session.textMessage(json);
                    } catch (JsonProcessingException e) {
                        log.error("메시지 JSON 변환 중 오류 발생", e);
                        return session.textMessage("메시지 처리 중 오류가 발생했습니다.");
                    }
                });
        
        // 입력과 출력 스트림 결합
        return Mono.zip(input, session.send(output)).then();
    }
} 