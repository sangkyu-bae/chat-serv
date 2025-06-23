package org.example.domain.chatroom.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.chat.dto.RequestCreateRoom;
import org.example.domain.chat.domain.SingleChatRoom;
import org.example.domain.ChatRoom;
import org.example.domain.chatroom.service.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<ChatRoom> getRooms(@RequestHeader("user-Id") String userId) {
        log.info("모든 채팅방 목록 조회 요청");
        return chatRoomService.findAllRooms(userId);
    }

    /**
     * 특정 채팅방 조회
     */
    @GetMapping("/rooms/{roomId}")
    public Mono<ResponseEntity<SingleChatRoom>> getRoom(@PathVariable String roomId) {
        log.info("채팅방 조회 요청. 방 ID: {}", roomId);
        return chatRoomService.findRoomById(roomId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 채팅방 생성
     */
    @PostMapping("/rooms")
    public Mono<ResponseEntity<ChatRoom>> createRoom(@Valid @RequestBody RequestCreateRoom requestCreateRoom,
                                                           @RequestHeader("user-Id") Long userId) {
        ChatRoom chatRoom = ChatRoom.create(requestCreateRoom.getJoinUserList());

        return chatRoomService.createSingleRoom(chatRoom)
                .map(ResponseEntity::ok);
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("/rooms/{roomId}")
    public Mono<ResponseEntity<String>> deleteRoom(@PathVariable String roomId) {
        return chatRoomService.deleteRoom(roomId)
                .map(deleted -> {
                    if (deleted) {
                        return ResponseEntity.ok("삭제 완료");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 채팅방");
                    }
                });
    }

} 