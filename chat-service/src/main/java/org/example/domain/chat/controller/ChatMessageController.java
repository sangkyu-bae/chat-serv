package org.example.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.example.domain.chat.domain.ChatMessage;
import org.example.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.chatroom.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "채팅 API", description = "채팅 관련 API")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅 메시지 전송 API", description = "채팅 메시지를 전송 합니다.")
    @PostMapping("/rooms/messages")
    public Mono<ResponseEntity<ChatMessage>> sendMessage(
            @RequestBody ChatMessage chatMessage) {

        String roomId = chatMessage.getRoomId();

        return null;

//        // 채팅방 존재 여부 확인
//        return chatRoomService.findRoomById(roomId)
//                .switchIfEmpty(Mono.error(new IllegalArgumentException("채팅방을 찾을 수 없습니다.")))
//                .flatMap(room -> {
//                    // 메시지에 방 ID 설정
//                    chatMessage.setRoomId(roomId);
//
//                    // 타임스탬프 설정
//                    if (chatMessage.getTimestamp() == 0) {
//                        chatMessage.setTimestamp(System.currentTimeMillis());
//                    }
//
//                    log.info("메시지 전송 요청: room={}, sender={}, type={}",
//                            roomId, chatMessage.getSender(), chatMessage.getType());
//
//                    // 메시지 전송
//                    return chatMessageService.sendMessageBySingleRoom(chatMessage)
//                            .thenReturn(ResponseEntity.ok(chatMessage));
//                })
//                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
    @Operation(summary = "채팅방 메시지 조회", description = "채팅방의 최근 메시지를 조회합니다.")
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