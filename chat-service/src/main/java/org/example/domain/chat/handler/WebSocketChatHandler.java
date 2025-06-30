package org.example.domain.chat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.chat.domain.ChatMessage;
import org.example.domain.chat.service.ChatMessageService;
import org.example.modules.websoket.server.StompEventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketChatHandler {

    private final ObjectMapper objectMapper;
    private final ChatMessageService chatMessageService;
    private final StompEventListener stompEventListener;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        log.info("STOMP 메시지 수신: room={}, sender={}, type={}",
                chatMessage.getRoomId(), chatMessage.getSender(), chatMessage.getType());

        // 메시지 타입에 따른 처리
        switch (chatMessage.getType()) {
            case ENTER:
            case TALK:
            case LEAVE:
                chatMessageService.sendMessage(chatMessage);
                break;
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // 사용자 추가 시 세션 정보 저장
        String sessionId = headerAccessor.getSessionId();
        String userId = chatMessage.getSender();
        
        stompEventListener.handleConnect(sessionId, userId);
        
        // 채팅방에 사용자 추가
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", chatMessage.getRoomId());
        
        log.info("사용자 추가: sessionId={}, userId={}, roomId={}", sessionId, userId, chatMessage.getRoomId());
    }
}