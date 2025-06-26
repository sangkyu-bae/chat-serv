package org.example.modules.websoket.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.module.redis.RedisKeyManager;
import org.example.modules.manager.ServerManager;
import org.example.modules.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import reactor.core.publisher.Mono;

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
        log.info("out");

        String sessionId = event.getSessionId();
        String sessionKey = redisKeyManager.getSessionKey(sessionId);

        redisRepository.findByHash(sessionKey)
                .flatMap(sessionInfo -> {
                    String userId = sessionInfo.get("userId");
                    log.info("Disconnected: sessionId={}, userId={}", sessionId, userId);

                    return Mono.when(
                            redisRepository.deleteHash(sessionKey),
                            redisRepository.deleteHash(redisKeyManager.getUserKey(userId)),
                            redisRepository.removeFromSet(redisKeyManager.getServerKey(serverId), userId)
                    );
                })
                .subscribe();
        log.info("Disconnected: sessionId={}, userId={}", sessionId);
    }

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        // 1번 세션 정보 - 유저의 연결된 서버정보, 연결 여부, 서버 번호, 파티션 번호

        log.info("connect");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String userId = accessor.getFirstNativeHeader("user-id");
        String sessionKey = redisKeyManager.getSessionKey(sessionId);
        String userKey = redisKeyManager.getUserKey(userId);

        Map<String,String> saveUser = Map.of(
                "sessionId",sessionId,
                "isConnect","true",
                "server",serverManager.getServerInfo(),
                "kafka", serverId
        );


        Map<String,String> saveSession = Map.of(
                "userId",userId,
                "isConnect","true",
                "server" ,serverManager.getServerInfo(),
                "kafka", serverId
        );

        try {
            Mono.when(
                    redisRepository.saveWithHash(sessionKey, saveSession),
                    redisRepository.saveWithHash(userKey, saveUser),
                    redisRepository.addToSet(redisKeyManager.getServerKey(serverId), userId)
            ).block(); // 동기 방식 보장
            log.info("WebSocket connected: sessionId={}, userId={}", sessionId, userId);
        } catch (Exception e) {
            log.error(" Error during connect event handling", e);
        }

    }
}
