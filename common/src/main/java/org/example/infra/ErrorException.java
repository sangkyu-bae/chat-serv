package org.example.infra;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorException extends RuntimeException{
    private final ErrorCode errorCode;

    private final String msg;

    public ErrorException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.msg = "";
    }
}
