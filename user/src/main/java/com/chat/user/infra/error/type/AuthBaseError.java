package com.chat.user.infra.error.type;

import com.chat.user.infra.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthBaseError implements ErrorCode {

    FAIL_AUTH(HttpStatus.BAD_REQUEST,"로그인 인증에 실패했어요.");

    private HttpStatus httpStatus;
    private String detail;
}
