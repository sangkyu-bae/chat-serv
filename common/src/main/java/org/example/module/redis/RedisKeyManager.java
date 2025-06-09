package org.example.module.redis;

import org.example.infra.RedisConfig;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyManager {
    private final RedisConfig redisConfig;

    public RedisKeyManager(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public String getRoomKey(String roomId) {
        return redisConfig.getRoomKeyPrefix() + roomId;
    }

    public String getMessageKey(String messageId) {
        return redisConfig.getMessageKeyPrefix() + messageId;
    }

    public String getSingChatRoomKey(String toUserId, String fromUserId){
        return getRoomKey(toUserId+":"+fromUserId);
    }

    public String getSessionKey(String sessionId){
        return redisConfig.getSessionKeyPrefix() + sessionId;
    }

    public String getUserKey(String userId){
        return redisConfig.getUserKeyPrefix() + userId;
    }

    public String getServerKey(String serverId){
        return redisConfig.getUserKeyPrefix() + serverId;
    }

} 