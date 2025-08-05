package com.chat.user.domain.account.service;

import com.chat.user.domain.account.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginService {

    private AuthenticationManager authenticationManager;

    public LoginService(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }
    public Authentication authenticate(LoginRequest loginRequest) {
        Authentication authentication = null;
        try {
            authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getId(),
                    loginRequest.getPassword()
            ));

        }catch (BadCredentialsException e){
            throw new BadCredentialsException("잘못된 패스워드 입니다.");
        }

        return authentication;
    }
}
