package com.chat.user.domain.account.service;

import com.chat.user.infra.error.DataBaseException;
import com.chat.user.infra.error.type.AuthBaseError;
import com.chat.user.module.jwt.JwtTokenProvider;
import com.chat.user.module.jwt.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final TokenManager tokenManager;

    private final JwtTokenProvider jwtTokenProvider;


    public String logout(String token){

        try{
            String userId = jwtTokenProvider.getUserId(token);
            tokenManager.logout(token);
        }catch (Exception e){
            throw new DataBaseException(AuthBaseError.FAIL_LOGOUT_AUTH,e.getMessage());
        }

        return "ok";
    }


}
