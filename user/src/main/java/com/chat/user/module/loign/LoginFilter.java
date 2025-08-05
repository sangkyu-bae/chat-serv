package com.chat.user.module.loign;

import com.chat.user.domain.account.dto.domain.Account;
import com.chat.user.domain.account.dto.domain.TokenInfo;
import com.chat.user.domain.account.dto.request.LoginRequest;
import com.chat.user.domain.account.service.TokenService;
import com.chat.user.infra.error.DataBaseException;
import com.chat.user.infra.error.type.AuthBaseError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.ErrorResponse;

import java.io.IOException;


@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        LoginRequest loginRequest = null;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse authentication request body");
        }

        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword())
            );
        }catch (BadCredentialsException e){
            new ObjectMapper().writeValue(response.getOutputStream(), e.getMessage());
        }
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserAccount userAccount = (UserAccount) authResult.getPrincipal();
        Account account = userAccount.getAccount();


        TokenInfo token = tokenService.createAndSaveTokens(account);
        new ObjectMapper().writeValue(response.getOutputStream(), token);
    }


}
