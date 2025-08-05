package com.chat.user.infra.error.type;

import com.chat.user.infra.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DataBaseError implements ErrorCode {


    UNIQUE_CONSTRAINT_VIOLATION(HttpStatus.CONFLICT, "데이터 제약 조건을 위반했습니다.");

    private HttpStatus httpStatus;
    private String detail;
}
