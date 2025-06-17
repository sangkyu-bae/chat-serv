package org.example.modules.websoket.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.chat.domain.SingleChatRoom;
import org.example.domain.chat.service.ChatRoomService;
import org.example.module.redis.RedisKeyManager;
import org.example.modules.manager.ServerManager;
import org.example.modules.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompEventListener {

    private final RedisKeyManager redisKeyManager;

    private final RedisRepository redisRepository;

    private final ServerManager serverManager;
    @Value("${kafka.server}")
    private String serverId;
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {

        String sessionId = event.getSessionId();
        String sessionKey = redisKeyManager.getSessionKey(sessionId);

        redisRepository.findByHash(sessionKey)
                .flatMap(sessionInfo -> {
                    String userId = sessionInfo.get("userId");
                    log.info("Disconnected: sessionId={}, userId={}", sessionId, userId);

                    return redisRepository.deleteHash(sessionKey)
                            .then(redisRepository.deleteHash(redisKeyManager.getUserKey(userId)));
                })
                .subscribe();
        log.info("Disconnected: sessionId={}, userId={}", sessionId);
    }

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        // 1번 세션 정보 - 유저의 연결된 서버정보, 연결 여부, 서버 번호, 파티션 번호

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String userId = accessor.getFirstNativeHeader("user-id");
        String roomId = accessor.getFirstNativeHeader("room-id");

        String sessionKey = redisKeyManager.getSessionKey(sessionId);
        String userKey = redisKeyManager.getUserKey(userId);

        Map<String,String> saveUser = Map.of(
                "sessionId",sessionId,
                "isConnect","true",
                "server",serverManager.getServerInfo(),
                "roomId", roomId  // 채팅방 정보 추가
        );


        Map<String,String> saveSession = Map.of(
                "userId",userId,
                "isConnect","true",
                "server" ,serverManager.getServerInfo(),
                "roomId", roomId  // 채팅방 정보 추가
        );

        redisRepository.saveWithHash(sessionKey,saveSession);
        redisRepository.saveWithHash(userKey,saveUser);

        redisRepository.addToSet(redisKeyManager.getServerKey(serverId),userId);


        log.info("Connected: sessionId={}, userId={}", sessionId, userId);

//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = accessor.getSessionId();
//        String userId = accessor.getFirstNativeHeader("user-id");
//        String roomId = accessor.getFirstNativeHeader("room-id");  // 채팅방 ID 추가
//
//        String sessionKey = redisKeyManager.getSessionKey(sessionId);
//        String userKey = redisKeyManager.getUserKey(userId);
//        String roomKey = redisKeyManager.getRoomKey(roomId);  // 채팅방 키 추가
//
//        Map<String,String> saveUser = Map.of(
//                "sessionId", sessionId,
//                "isConnect", "true",
//                "server", serverManager.getServerInfo(),
//                "roomId", roomId  // 채팅방 정보 추가
//        );
//
//        Map<String,String> saveSession = Map.of(
//                "userId", userId,
//                "isConnect", "true",
//                "server", serverManager.getServerInfo(),
//                "roomId", roomId  // 채팅방 정보 추가
//        );
//
//        // 세션 정보 저장
//        redisRepository.saveWithHash(sessionKey, saveSession)
//                // 사용자 정보 저장
//                .then(redisRepository.saveWithHash(userKey, saveUser))
//                // 채팅방에 사용자 추가
//                .then(redisRepository.addToSet(roomKey + ":users", userId))
//                // 서버의 채팅방 목록에 추가
//                .then(redisRepository.addToSet("server:" + serverManager.getServerInfo() + ":rooms", roomId))
//                .subscribe();
//
//        log.info("Connected: sessionId={}, userId={}, roomId={}", sessionId, userId, roomId);


    }
}
