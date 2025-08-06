package com.chat.user.domain.account.repositroy;

import com.chat.user.module.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TokenRepository {

    private final RedisRepository redisRepository;
    
    private static final String ACCESS_TOKEN_PREFIX = "access_token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    /**
     * Access Token 저장
     */
    public void saveAccessToken(String userId, String accessToken, long expirationTime) {
        String key = ACCESS_TOKEN_PREFIX + userId;
        redisRepository.setString(key, accessToken, Duration.ofMillis(expirationTime));
        log.info("Access token saved for user: {}", userId);
    }

    /**
     * Refresh Token 저장
     */
    public void saveRefreshToken(String userId, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisRepository.setString(key, refreshToken, Duration.ofMillis(expirationTime));
        log.info("Refresh token saved for user: {}", userId);
    }

    /**
     * Access Token 조회
     */
    public String getAccessToken(String userId) {
        String key = ACCESS_TOKEN_PREFIX + userId;
        Object value = redisRepository.getString(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        Object value = redisRepository.getString(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Access Token 삭제
     */
    public void deleteAccessToken(String userId) {
        String key = ACCESS_TOKEN_PREFIX + userId;
        redisRepository.deleteString(key);
        log.info("Access token deleted for user: {}", userId);
    }

    /**
     * Refresh Token 삭제
     */
    public void deleteRefreshToken(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisRepository.deleteString(key);
        log.info("Refresh token deleted for user: {}", userId);
    }

    /**
     * 모든 토큰 삭제 (로그아웃)
     */
    public void deleteAllTokens(String userId) {
        deleteAccessToken(userId);
        deleteRefreshToken(userId);
        log.info("All tokens deleted for user: {}", userId);
    }

    /**
     * 토큰을 블랙리스트에 추가
     */
    public void addToBlacklist(String token, long expirationTime) {
        String key = BLACKLIST_PREFIX + token;
        redisRepository.setString(key, "blacklisted", Duration.ofMillis(expirationTime));
        log.info("Token added to blacklist: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisRepository.hasKey(key);
    }

    /**
     * 사용자의 토큰 존재 여부 확인
     */
    public boolean hasValidToken(String userId) {
        String accessKey = ACCESS_TOKEN_PREFIX + userId;
        String refreshKey = REFRESH_TOKEN_PREFIX + userId;
        
        return redisRepository.hasKey(accessKey) || redisRepository.hasKey(refreshKey);
    }

    /**
     * 토큰 만료 시간 조회
     */
    public Long getTokenExpiration(String userId, String tokenType) {
        String key = (tokenType.equals("access") ? ACCESS_TOKEN_PREFIX : REFRESH_TOKEN_PREFIX) + userId;
        return redisRepository.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * 토큰 갱신 (기존 토큰 삭제 후 새 토큰 저장)
     */
    public void refreshTokens(String userId, String newAccessToken, String newRefreshToken, 
                            long accessExpiration, long refreshExpiration) {
        // 기존 토큰들을 블랙리스트에 추가
        String oldAccessToken = getAccessToken(userId);
        String oldRefreshToken = getRefreshToken(userId);
        
        if (oldAccessToken != null) {
            addToBlacklist(oldAccessToken, accessExpiration);
        }
        if (oldRefreshToken != null) {
            addToBlacklist(oldRefreshToken, refreshExpiration);
        }
        
        // 새 토큰 저장
        saveAccessToken(userId, newAccessToken, accessExpiration);
        saveRefreshToken(userId, newRefreshToken, refreshExpiration);
        
        log.info("Tokens refreshed for user: {}", userId);
    }
} 