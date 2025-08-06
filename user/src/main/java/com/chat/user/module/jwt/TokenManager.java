package com.chat.user.module.jwt;

import com.chat.user.domain.account.dto.domain.Account;
import com.chat.user.domain.account.dto.domain.TokenInfo;
import com.chat.user.domain.account.repositroy.TokenRepository;
import com.chat.user.module.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenManager {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인 시 토큰 생성 및 저장
     */
    public TokenInfo createAndSaveTokens(Account account) {
        String userId = account.getUserId();
        
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(account);
        String refreshToken = jwtTokenProvider.getRefreshToken(account);
        
        // 토큰 만료 시간 계산
        Date accessExpiredTime = jwtTokenProvider.getExpiredTime(accessToken);
        Date refreshExpiredTime = jwtTokenProvider.getExpiredTime(refreshToken);
        
        long accessExpiration = accessExpiredTime.getTime() - System.currentTimeMillis();
        long refreshExpiration = refreshExpiredTime.getTime() - System.currentTimeMillis();
        
        // Redis에 토큰 저장
        tokenRepository.saveAccessToken(userId, accessToken, accessExpiration);
        tokenRepository.saveRefreshToken(userId, refreshToken, refreshExpiration);
        
        log.info("Tokens created and saved for user: {}", userId);
        
        return TokenInfo.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiration(accessExpiration)
                .refreshTokenExpiration(refreshExpiration)
                .tokenType("Bearer")
                .isBlacklisted(false)
                .build();
    }

    /**
     * 토큰 갱신
     */
    public TokenInfo refreshTokens(String userId, String refreshToken) {
        // 기존 refresh token 검증
        String storedRefreshToken = tokenRepository.getRefreshToken(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        // 블랙리스트 확인
        if (tokenRepository.isBlacklisted(refreshToken)) {
            throw new IllegalArgumentException("Refresh token is blacklisted");
        }
        
        // 새로운 토큰 생성
        Account account = new Account(); // 실제로는 AccountService에서 조회해야 함
        account.setUserId(userId);
        
        String newAccessToken = jwtTokenProvider.generateToken(account);
        String newRefreshToken = jwtTokenProvider.getRefreshToken(account);
        
        Date newAccessExpiredTime = jwtTokenProvider.getExpiredTime(newAccessToken);
        Date newRefreshExpiredTime = jwtTokenProvider.getExpiredTime(newRefreshToken);
        
        long newAccessExpiration = newAccessExpiredTime.getTime() - System.currentTimeMillis();
        long newRefreshExpiration = newRefreshExpiredTime.getTime() - System.currentTimeMillis();
        
        // 토큰 갱신
        tokenRepository.refreshTokens(userId, newAccessToken, newRefreshToken, 
                                    newAccessExpiration, newRefreshExpiration);
        
        log.info("Tokens refreshed for user: {}", userId);
        
        return TokenInfo.builder()
                .userId(userId)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiration(newAccessExpiration)
                .refreshTokenExpiration(newRefreshExpiration)
                .tokenType("Bearer")
                .isBlacklisted(false)
                .build();
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        // JWT 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(token)) {
            return false;
        }
        
        // 블랙리스트 확인
        if (tokenRepository.isBlacklisted(token)) {
            log.warn("Token is blacklisted: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
            return false;
        }
        
        return true;
    }

    /**
     * 로그아웃 (토큰 블랙리스트 추가)
     */
    public void logout(String userId) {
        String accessToken = tokenRepository.getAccessToken(userId);
        String refreshToken = tokenRepository.getRefreshToken(userId);
        
        if (accessToken != null) {
            Date expiredTime = jwtTokenProvider.getExpiredTime(accessToken);
            long expiration = expiredTime.getTime() - System.currentTimeMillis();
            tokenRepository.addToBlacklist(accessToken, expiration);
        }
        
        if (refreshToken != null) {
            Date expiredTime = jwtTokenProvider.getExpiredTime(refreshToken);
            long expiration = expiredTime.getTime() - System.currentTimeMillis();
            tokenRepository.addToBlacklist(refreshToken, expiration);
        }
        
        // Redis에서 토큰 삭제
        tokenRepository.deleteAllTokens(userId);
        
        log.info("User logged out: {}", userId);
    }

    /**
     * 토큰 정보 조회
     */
    public TokenInfo getTokenInfo(String userId) {
        String accessToken = tokenRepository.getAccessToken(userId);
        String refreshToken = tokenRepository.getRefreshToken(userId);
        
        if (accessToken == null && refreshToken == null) {
            return null;
        }
        
        Long accessExpiration = tokenRepository.getTokenExpiration(userId, "access");
        Long refreshExpiration = tokenRepository.getTokenExpiration(userId, "refresh");
        
        boolean isAccessBlacklisted = accessToken != null && tokenRepository.isBlacklisted(accessToken);
        boolean isRefreshBlacklisted = refreshToken != null && tokenRepository.isBlacklisted(refreshToken);
        
        return TokenInfo.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiration(accessExpiration != null ? accessExpiration : 0)
                .refreshTokenExpiration(refreshExpiration != null ? refreshExpiration : 0)
                .tokenType("Bearer")
                .isBlacklisted(isAccessBlacklisted || isRefreshBlacklisted)
                .build();
    }

    /**
     * 사용자의 모든 토큰 강제 만료
     */
    public void forceExpireAllTokens(String userId) {
        tokenRepository.deleteAllTokens(userId);
        log.info("All tokens force expired for user: {}", userId);
    }
} 