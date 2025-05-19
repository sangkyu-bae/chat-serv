package org.example.chat.service;

import com.example.chatservicenetty.domain.chat.model.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
public class ChatRoomService {

    // 채팅방 목록을 저장하는 맵 (나중에 DB로 대체 가능)
    private final Map<String, ChatRoom> chatRooms = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 모든 채팅방 목록을 반환
     */
    public Flux<ChatRoom> findAllRooms() {
        return Flux.fromIterable(chatRooms.values());
    }

    /**
     * 특정 채팅방 조회
     */
    public Mono<ChatRoom> findRoomById(String roomId) {
        return Mono.justOrEmpty(chatRooms.get(roomId));
    }

    /**
     * 채팅방 생성
     */
    public Mono<ChatRoom> createRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        log.info("채팅방이 생성되었습니다. 방 ID: {}, 방 이름: {}", chatRoom.getRoomId(), chatRoom.getName());
        return Mono.just(chatRoom);
    }

    /**
     * 채팅방 삭제
     */
    public Mono<Void> deleteRoom(String roomId) {
        ChatRoom room = chatRooms.remove(roomId);
        if (room != null) {
            log.info("채팅방이 삭제되었습니다. 방 ID: {}, 방 이름: {}", room.getRoomId(), room.getName());
        }
        return Mono.empty();
    }
} 