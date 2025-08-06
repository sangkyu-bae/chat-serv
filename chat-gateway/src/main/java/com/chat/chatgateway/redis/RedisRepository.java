package com.chat.chatgateway.redis;

import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    /**
     * 키 존재 여부 확인
     */
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    public Mono<Boolean> hasKey(String key) {
        return reactiveRedisTemplate.hasKey(key)
                .onErrorReturn(false); // 에러 발생 시 false 반환
    }


}
