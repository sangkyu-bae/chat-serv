package com.chat.user.domain.accouSnt.controller;

import com.chat.user.domain.account.service.AuthService;
import com.chat.user.infra.api.ChatApiResponse;
import com.chat.user.module.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthService authService;
    @Operation(summary = "회원 로그아웃 ", description = "회원 로그아웃.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "로그아웃 실패")
    })
    @DeleteMapping("/")
    public ChatApiResponse<String> logout(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        return ChatApiResponse.ok(authService.logout(token));
    }
}
