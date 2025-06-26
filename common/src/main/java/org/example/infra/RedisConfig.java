package org.example.infra;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.key.room}")
    private String roomKeyPrefix;

    @Value("${redis.key.message}")
    private String messageKeyPrefix;

    @Value("${redis.key.session}")
    private String sessionKeyPrefix;

    @Value("${redis.key.user}")
    private String userKeyPrefix;

    @Value("${redis.key.server}")
    private String serverPrefix;

    @Value("${redis.key.join-user}")
    private String joinPrefix;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        StringRedisSerializer serializer = new StringRedisSerializer();
        RedisSerializationContext.RedisSerializationContextBuilder<String, String> builder =
                RedisSerializationContext.newSerializationContext();

        RedisSerializationContext<String, String> context = builder
                .key(serializer)
                .value(serializer)
                .hashKey(serializer)
                .hashValue(serializer)
                .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }


    @Bean
    public RedisAsyncCommands<String, String> redisAsyncCommands() {
        RedisClient redisClient = RedisClient.create("redis://" + host + ":" + port);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        return connection.async();
    }

    // Redis Key Prefix 접근을 위한 getter 메서드
    public String getRoomKeyPrefix() {
        return roomKeyPrefix;
    }

    public String getRoomToUserKeyPrefix(){
        return joinPrefix;
    }

    public String getMessageKeyPrefix() {
        return messageKeyPrefix;
    }

    public String getSessionKeyPrefix(){
        return sessionKeyPrefix;
    }

    public String getUserKeyPrefix(){
        return userKeyPrefix;
    }

    public String getServerPrefix(){
        return serverPrefix;
    }
}
