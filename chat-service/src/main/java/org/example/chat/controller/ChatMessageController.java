package org.example.chat.controller;

import org.example.chat.model.ChatMessage;
import org.example.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chat.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    /**
     * 채팅 메시지 전송
     */
    @PostMapping("/rooms/{roomId}/messages")
    public Mono<ResponseEntity<ChatMessage>> sendMessage(
            @PathVariable String roomId,
            @RequestBody ChatMessage chatMessage) {
        
        // 채팅방 존재 여부 확인
        return chatRoomService.findRoomById(roomId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("채팅방을 찾을 수 없습니다.")))
                .flatMap(room -> {
                    // 메시지에 방 ID 설정
                    chatMessage.setRoomId(roomId);
                    
                    // 타임스탬프 설정
                    if (chatMessage.getTimestamp() == 0) {
                        chatMessage.setTimestamp(System.currentTimeMillis());
                    }
                    
                    log.info("메시지 전송 요청: room={}, sender={}, type={}", 
                            roomId, chatMessage.getSender(), chatMessage.getType());
                    
                    // 메시지 전송
                    return chatMessageService.sendMessage(chatMessage)
                            .thenReturn(ResponseEntity.ok(chatMessage));
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
    
    /**
     * 채팅방의 최근 메시지 조회
     */
    @GetMapping("/rooms/{roomId}/messages")
    public Mono<ResponseEntity<Flux<ChatMessage>>> getMessages(@PathVariable String roomId) {
        // 채팅방 존재 여부 확인
        return chatRoomService.findRoomById(roomId)
                .map(room -> {
                    log.info("채팅방 메시지 조회 요청: room={}", roomId);
                    
                    // 최근 메시지 조회
                    Flux<ChatMessage> messages = chatMessageService.getRecentMessages(roomId);
                    
                    return ResponseEntity.ok(messages);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 