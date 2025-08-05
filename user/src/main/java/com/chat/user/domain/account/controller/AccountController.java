package com.chat.user.domain.account.controller;

import com.chat.user.domain.account.dto.domain.Account;
import com.chat.user.domain.account.dto.request.RegisterAccount;
import com.chat.user.domain.account.service.AccountService;
import com.chat.user.domain.account.valid.RegisterAccountValid;
import com.chat.user.infra.api.ChatApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/account")
@Tag(name = "유저 관리 API", description = "유저 관리 API")
public class AccountController {


    private final AccountService accountService;

    private final RegisterAccountValid registerAccountValid;

    @InitBinder("registerAccount")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(registerAccountValid);
    }

    @Operation(summary = "유저 등록 ", description = "유저 회원가입.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "404", description = "등록 실패")
    })
    @PostMapping("/")
    public ChatApiResponse<?> registerUser(@Valid @RequestBody RegisterAccount registerAccount){
        Account account = accountService.registerAccount(registerAccount);
        return ChatApiResponse.ok(account);
    }

}
