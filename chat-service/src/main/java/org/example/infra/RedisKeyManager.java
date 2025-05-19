package org.example.infra;

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
} 