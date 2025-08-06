package com.chat.user.module.jwt;

import com.chat.user.domain.account.dto.domain.Account;
import com.chat.user.domain.account.entity.AccountEntity;
import com.chat.user.domain.account.service.AccountService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private String SECRET;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.token-time}")
    private long ACCESS_TOKEN_TIME;

    @Value("${jwt.refresh.token-time}")
    private long ACCESS_REFRESH_TOKEN_TIME;
    @PostConstruct
    protected void init(){
        SECRET = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }



    public String generateToken(Account account) {

        List<String> roleList = account.getRoleList();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",roleList);

        return doGenerateToken(claims, account.getUserId(),ACCESS_TOKEN_TIME);
    }

    public String getRefreshToken(Account Account){
        return doGenerateToken(new HashMap<>(),Account.getUserId(),ACCESS_REFRESH_TOKEN_TIME);
    }
    private String doGenerateToken(Map<String, Object> claims, String subject,long tokenTime) {
        return Jwts.builder().
                setClaims(claims).
                setSubject(subject).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis() + tokenTime)).
                signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }
    //추가
    public Date getExpiredTime(String token) {
        return getClaimsFromJwtToken(token).getExpiration();
    }

    private Claims getClaimsFromJwtToken(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    // 토큰에서 회원 정보 추출
    public String getUserId(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        if(request.getHeader("Authorization") != null )
            return request.getHeader("Authorization").substring(7);
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
