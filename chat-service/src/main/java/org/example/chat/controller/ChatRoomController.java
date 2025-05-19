package org.example.chat.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chat.model.ChatRoom;
import org.example.chat.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 모든 채팅방 목록 조회
     */
    @GetMapping("/rooms")
    public Flux<ChatRoom> getRooms() {
        log.info("모든 채팅방 목록 조회 요청");
        return chatRoomService.findAllRooms();
    }

    /**
     * 특정 채팅방 조회
     */
    @GetMapping("/rooms/{roomId}")
    public Mono<ResponseEntity<ChatRoom>> getRoom(@PathVariable String roomId) {
        log.info("채팅방 조회 요청. 방 ID: {}", roomId);
        return chatRoomService.findRoomById(roomId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 채팅방 생성
     */
    @PostMapping("/rooms")
    public Mono<ResponseEntity<ChatRoom>> createRoom(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        log.info("채팅방 생성 요청. 방 이름: {}", name);
        
        if (name == null || name.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        return chatRoomService.createRoom(name)
                .map(ResponseEntity::ok);
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("/rooms/{roomId}")
    public Mono<ResponseEntity<Void>> deleteRoom(@PathVariable String roomId) {
        log.info("채팅방 삭제 요청. 방 ID: {}", roomId);
        
        return chatRoomService.findRoomById(roomId)
                .flatMap(room -> chatRoomService.deleteRoom(roomId)
                        .then(Mono.just(ResponseEntity.ok().<Void>build())))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 