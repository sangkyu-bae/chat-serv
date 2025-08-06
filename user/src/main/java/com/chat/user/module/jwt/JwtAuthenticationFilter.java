//package com.chat.user.module.jwt;
//
//import com.chat.user.domain.account.service.TokenService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final TokenService tokenService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                  FilterChain filterChain) throws ServletException, IOException {
//
//        String token = jwtTokenProvider.resolveToken(request);
//
//        if (StringUtils.hasText(token)) {
//            try {
//                // 토큰 유효성 검증 (JWT + Redis 블랙리스트)
//                if (tokenService.validateToken(token)) {
//                    // 인증 정보 설정
//                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    log.debug("JWT 토큰 인증 성공: {}", authentication.getName());
//                } else {
//                    log.warn("JWT 토큰 검증 실패: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
//                }
//            } catch (Exception e) {
//                log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage());
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}