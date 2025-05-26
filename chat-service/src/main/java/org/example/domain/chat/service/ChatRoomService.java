package org.example.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.chat.model.ChatRoom;
import org.example.infra.RedisKeyManager;
import org.example.modules.redis.RedisRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    // 채팅방 목록을 저장하는 맵 (나중에 DB로 대체 가능)
    private final Map<String, ChatRoom> chatRooms = Collections.synchronizedMap(new LinkedHashMap<>());

    private final RedisKeyManager redisKeyManager;

    private final RedisRepository redisRepository;

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
        String key = redisKeyManager.getRoomKey(name);

        LocalDateTime now = LocalDateTime.now();

        Map<String,String> param = Map.of(
                "isDeleted","false",
                "createAt",now.toString()
        ) ;

        redisRepository.saveWithHash(key,param);//채팅방 정보 생성
        redisRepository.saveWhitList(key,name); //유저 정보에 채팅방 정보 추가

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