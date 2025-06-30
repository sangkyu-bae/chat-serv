package org.example.modules.websoket.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.modules.manager.RedisKeyManager;
import org.example.modules.manager.ServerManager;
import org.example.modules.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompEventListener implements ApplicationListener<SessionDisconnectEvent> {

    private final RedisKeyManager redisKeyManager;

    private final RedisRepository redisRepository;

    private final ServerManager serverManager;
    @Value("${kafka.server}")
    private String serverId;
    
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        handleDisconnectEvent(event.getSessionId());
    }
    
    public void handleDisconnectEvent(String sessionId) {
        log.info("out");

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
                .doOnSuccess(result -> log.info("Disconnected: sessionId={}", sessionId))
                .doOnError(error -> log.error("Error during disconnect event handling", error))
                .subscribe();
    }

    public void handleConnect(String sessionId, String userId) {
        // 1번 세션 정보 - 유저의 연결된 서버정보, 연결 여부, 서버 번호, 파티션 번호

        log.info("connect");
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

        Mono.when(
                redisRepository.saveWithHash(sessionKey, saveSession),
                redisRepository.saveWithHash(userKey, saveUser),
                redisRepository.addToSet(redisKeyManager.getServerKey(serverId), userId)
        ).doOnSuccess(result -> log.info("WebSocket connected: sessionId={}, userId={}", sessionId, userId))
          .doOnError(error -> log.error("Error during connect event handling", error))
          .subscribe();
    }
}
