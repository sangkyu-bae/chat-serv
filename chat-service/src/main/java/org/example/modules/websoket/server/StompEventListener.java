package org.example.modules.websoket.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.module.redis.RedisKeyManager;
import org.example.modules.manager.ServerManager;
import org.example.modules.redis.RedisRepository;
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
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String userId = accessor.getFirstNativeHeader("user-id");

        String sessionKey = redisKeyManager.getSessionKey(sessionId);
        String userKey = redisKeyManager.getUserKey(userId);

        Map<String,String> saveUser = Map.of(
                "sessionId",sessionId,
                "isConnect","true",
                "server",serverManager.getServerInfo()
        );


        Map<String,String> saveSession = Map.of(
                "userId",userId,
                "isConnect","true",
                "server" ,serverManager.getServerInfo()
        );

        redisRepository.saveWithHash(sessionKey,saveSession);
        redisRepository.saveWithHash(userKey,saveUser);


        log.info("Connected: sessionId={}, userId={}", sessionId, userId);
    }
}
