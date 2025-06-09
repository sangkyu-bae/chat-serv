package org.example.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.ChatMessage;
import org.example.domain.chat.domain.SingleChatRoom;
import org.example.module.redis.RedisKeyManager;
import org.example.modules.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Map<String, SingleChatRoom> chatRooms = Collections.synchronizedMap(new LinkedHashMap<>());
    private final RedisKeyManager redisKeyManager;
    private final RedisRepository redisRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${kafka.server.id}")
    private String serverId;

    /**
     * 모든 채팅방 목록을 반환
     */
    public Flux<SingleChatRoom> findAllRooms() {
        return Flux.fromIterable(chatRooms.values());
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
    public Mono<SingleChatRoom> createSingleRoom(SingleChatRoom singleChatRoom) {

        String key = redisKeyManager.getSingChatRoomKey(singleChatRoom.getToUser(),singleChatRoom.getFromUser());

        redisRepository.saveWithHash(key,singleChatRoom.getRoomByMap());//채팅방 정보 생성

//        redisRepository.saveWhitList(singleChatRoom.getFromUser(),key); //유저 정보에 채팅방 정보 추가
//        redisRepository.saveWhitList(singleChatRoom.getToUser(),key); //유저 정보에 채팅방 정보 추가

        redisRepository.saveWhitList(key,singleChatRoom.getFromUser());
        redisRepository.saveWhitList(key,singleChatRoom.getToUser());

        return Mono.just(singleChatRoom);
    }

    /**
     * 채팅방 삭제
     */
    public Mono<Void> deleteRoom(String roomId) {
        SingleChatRoom room = chatRooms.remove(roomId);
//        if (room != null) {
//            log.info("채팅방이 삭제되었습니다. 방 ID: {}, 방 이름: {}", room.getRoomId(), room.getName());
//        }
        return Mono.empty();
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