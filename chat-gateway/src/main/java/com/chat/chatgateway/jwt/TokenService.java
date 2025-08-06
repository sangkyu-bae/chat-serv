package com.chat.chatgateway.jwt;

import com.chat.chatgateway.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RedisRepository redisRepository;

    @Value("${redis.key.black-list}")
    private String BLACK_LIST_PREFIX;

    public Mono<Boolean> isBlackListToken(String userId){
        return redisRepository.hasKey(BLACK_LIST_PREFIX + userId)
                .doOnNext(isBlacklisted -> {
                    if (isBlacklisted) {
                        log.info("User [{}] is in blacklist", userId);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error checking blacklist token for userId: {}", userId, e);
                    return Mono.just(false); // 에러 시 기본값 처리
                });
    }


}
