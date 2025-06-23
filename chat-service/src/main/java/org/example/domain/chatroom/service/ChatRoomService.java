package org.example.domain.chatroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.ChatMessage;
import org.example.domain.chat.domain.SingleChatRoom;
import org.example.domain.ChatRoom;
import org.example.request.RequestChatRoom;
import org.example.module.converter.JsonConverter;
import org.example.module.redis.RedisKeyManager;
import org.example.modules.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Map<String, SingleChatRoom> chatRooms = Collections.synchronizedMap(new LinkedHashMap<>());
    private final RedisKeyManager redisKeyManager;
    private final RedisRepository redisRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final JsonConverter jsonConverter;

    @Value("${kafka.server.id}")
    private String serverId;

    /**
     * 모든 채팅방 목록을 반환
     */
    public Flux<ChatRoom> findAllRooms(String userId) {
        return redisRepository.getAllSetMembersAsSet(userId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(key ->
                        redisRepository.findByHash(key)
                                .map(hash -> ChatRoom.create(new ArrayList<>(), hash, key))
                );
    }

    /**
     * 특정 채팅방 조회
     */
    public Mono<SingleChatRoom> findRoomById(String roomId) {
        return Mono.justOrEmpty(chatRooms.get(roomId));
    }

    /**
     * 채팅방 생성
     */
    public Mono<ChatRoom> createSingleRoom(ChatRoom chatRoom) {
        String roomKey = redisKeyManager.getRoomKey(chatRoom.getRoomKey());
        Map<String, String> request = jsonConverter.toMap(RequestChatRoom.from(chatRoom));

        // 1. 메타정보 저장
        Mono<Boolean> saveHash = redisRepository.saveWithHash(roomKey, request);

        // 2. 참여자 리스트 저장
        Flux<Long> saveUserToRoom = Flux.fromIterable(chatRoom.getJoinList())
                .flatMap(userId -> {
                    String userKey = redisKeyManager.getUserKey(userId);
                    return redisRepository.addToSet(userKey, chatRoom.getRoomKey());
                });

        // 3. 채팅방 참여자 저장
        Flux<Long> saveRoomToUsers = Flux.fromIterable(chatRoom.getJoinList())
                .flatMap(userId -> {
                    return redisRepository.addToSet(roomKey, userId);
                });

        return Mono.when(
                        saveHash,
                        saveUserToRoom.then(),
                        saveRoomToUsers.then()
                )
                .doOnSuccess(v -> log.info("채팅방 생성 성공: {}", roomKey))
                .doOnError(e -> log.error("채팅방 생성 실패: {}", e.getMessage()))
                .thenReturn(chatRoom);
    }

    /**
     * 채팅방 삭제
     */
    public Mono<Boolean> deleteRoom(String roomId) {
        String key = redisKeyManager.getRoomKey(roomId);
        return redisRepository.deleteHash(key);
    }


//    public Mono<>

    public Mono<Void> test(ChatMessage chatMessage) {
        return redisRepository.findBySet(redisKeyManager.getServerKey(serverId))
            .filter(userId -> chatMessage.getToUsers().contains(userId))
            .collectList()
            .flatMap(targetUsers -> 
                Flux.fromIterable(targetUsers)
                    .flatMap(userId -> 
                        redisRepository.findByHash(redisKeyManager.getUserKey(userId))
                            .doOnNext(userInfo -> {
                                String roomId = userInfo.get("roomId");
                                // roomId를 기준으로 메시지 전송
                                
                            })
                    )
                    .then()
            );
    }
} 