package com.chat.user.infra.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DataBaseException extends RuntimeException{
    ErrorCode errorCode;
    String resMsg;
}
