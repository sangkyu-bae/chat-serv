package org.example.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.module.converter.JsonConverter;
import org.example.common.converter.DateTimeConverter;
import org.example.domain.ChatMessage;
import org.example.domain.chat.domain.SendMessageSaver;
import org.example.module.kafka.KafkaManager;
import org.example.module.redis.RedisKeyManager;
import org.example.modules.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅 메시지 처리를 담당하는 서비스
 * WebFlux 기반 리액티브 방식으로 메시지 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ObjectMapper objectMapper;
    
    // 채팅방 별 메시지 저장소 (최근 50개 메시지만 저장)
    private final Map<String, List<ChatMessage>> chatMessageRepository = new ConcurrentHashMap<>();
    
    // 채팅방 별 메시지 Sink (Many-to-Many 브로드캐스트)
    private final Map<String, Sinks.Many<ChatMessage>> chatSinks = new ConcurrentHashMap<>();

    private final KafkaManager kafkaManager;

    private final RedisRepository redisRepository;

    private final JsonConverter jsonConverter;

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private final RedisKeyManager redisKeyManager;
    @Value("${redis.key.chat-room}")
    private String chatRoomKey;

    @Value("${kafka.topic.chat}")
    private String chatTopic;
//    /**
//     * 메시지 전송
//     * @param chatMessage 전송할 채팅 메시지
//     */
//    public Mono<Void> sendMessage(ChatMessage chatMessage) {
//        kafkaService.send(chatTopic,chatMessage);
//        return Mono.empty();
//    }


//    /**
//     * 메시지 전송
//     * @param chatMessage 전송할 채팅 메시지
//     */
//    public Mono<Void> sendMessage(ChatMessage chatMessage) {
//        String roomId = chatMessage.getRoomId();
//
//        // 메시지 저장
//        saveMessage(chatMessage);
//
//        // Sink를 통해 메시지 발행
//        Sinks.Many<ChatMessage> sink = getSink(roomId);
//        sink.tryEmitNext(chatMessage);
//
//        log.info("메시지 전송 완료: room={}, sender={}, type={}",
//                roomId, chatMessage.getSender(), chatMessage.getType());
//
//        return Mono.empty();
//    }
    
    /**
     * 메시지 저장 (최근 50개만 유지)
     */
    private void saveMessage(ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId();
        chatMessageRepository.computeIfAbsent(roomId, k -> new ArrayList<>())
                .add(chatMessage);
        
        // 최대 50개 메시지만 유지
        List<ChatMessage> messages = chatMessageRepository.get(roomId);
        if (messages.size() > 50) {
            messages.remove(0);
        }
    }

    /**
     * 메시지 전송
     * @param message 전송할 채팅 메시지
     */
    public void sendMessageBySingleRoom(ChatMessage message) {

        String userId = message.getSender();

        redisRepository.findByHash(redisKeyManager.getRoomKey(message.getRoomId()))
                .flatMap(roomInfo -> {
//                    if (roomInfo == null) {
//                        return Mono.error(new RoomNotFoundException(message.getRoomId()));
//                    }

                    String messageKey = redisKeyManager.getMessageKey(message.getRoomId());

                    SendMessageSaver save = SendMessageSaver.createGenerate(
                            SendMessageSaver.StatusType.PENDING,
                            jsonConverter.toJson(message),
                            DateTimeConverter.getToString14HHMI()
                    );
                    Map<String, String> saveMessage = jsonConverter.toMap(save);

                    return redisRepository.saveWithHash(messageKey, saveMessage)
                            .then(kafkaManager.sendByAsync("chat-topic", message.getRoomId(), message));
                })
                .doOnSuccess(result -> {
                    log.info("✅ Kafka 메시지 전송 성공: {}", message);
                })
                .doOnError(error -> {
                    log.error("❌ Kafka 메시지 전송 실패: {}", error.getMessage(), error);

                    // WebSocket으로 사용자에게 실패 알림 전송
//                    socketSessionManager.sendToUser(message.getSender(),
//                            new SocketMessage("FAILED", message.getRoomId(), message.getContent()));
                })
                .onErrorResume(error -> {
                    // 실패 메시지 Redis에 저장
                    return reactiveRedisTemplate.opsForHash()
                            .putAll("chat:message:" + message.getRoomId(),
                                    Map.of(
                                            "status", "FAILED",
                                            "message", jsonConverter.toJson(message),
                                            "timestamp", String.valueOf(System.currentTimeMillis())
                                    ))
                            .then(Mono.empty()); // 오류는 여기서 swallow
                })
                .subscribe(); // Fire-and-forget

    }
    
    /**
     * 채팅방의 메시지 Sink 가져오기 (없으면 생성)
     */
    private Sinks.Many<ChatMessage> getSink(String roomId) {
        return chatSinks.computeIfAbsent(roomId, k -> 
            Sinks.many().multicast().onBackpressureBuffer());
    }
    
    /**
     * 특정 채팅방의 메시지 구독
     * @param roomId 채팅방 ID
     * @return 메시지 스트림
     */
    public Flux<ChatMessage> joinRoom(String roomId,String senderId) {


        return getSink(roomId).asFlux();
    }
    
    /**
     * 특정 채팅방의 최근 메시지 조회
     */
    public Flux<ChatMessage> getRecentMessages(String roomId) {
        List<ChatMessage> messages = chatMessageRepository.getOrDefault(roomId, new ArrayList<>());
        return Flux.fromIterable(messages);
    }
} 