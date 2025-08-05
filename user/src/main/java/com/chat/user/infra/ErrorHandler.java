package com.chat.user.infra;

import com.chat.user.infra.api.ChatApiResponse;
import com.chat.user.infra.error.DataBaseException;
import com.chat.user.infra.error.ErrorCode;
import com.chat.user.infra.error.ErrorException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Hidden
public class ErrorHandler {


    @ExceptionHandler(value = {DataBaseException.class})
    public ChatApiResponse<Object> errorException(DataBaseException e){
        e.printStackTrace();
        log.error("errorException throw code : {}", e.getErrorCode());
        log.error("errorException throw msg : {}", e.getErrorCode());
        ErrorCode errorCode = e.getErrorCode();
        return ChatApiResponse.of(
                errorCode.getHttpStatus(),
                e.getResMsg(),
                null
        );
    }


    @ExceptionHandler(value = {ErrorException.class})
    public ChatApiResponse<Object> errorException(ErrorException e){
        e.printStackTrace();
        log.error("errorException throw code : {}", e.getErrorCode());
        log.error("errorException throw msg : {}", e.getMsg());
        ErrorCode errorCode = e.getErrorCode();
        return ChatApiResponse.of(
                errorCode.getHttpStatus(),
                errorCode.getDetail(),
                null
        );
    }

    @ExceptionHandler(value = {BindException.class})
    public ChatApiResponse<Object> bindException(BindException e){
        String resMsg = String.format("%s : %s",
                    e.getBindingResult().getAllErrors().get(0).getObjectName(),
                    e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
                );
        return ChatApiResponse.of(
                HttpStatus.BAD_REQUEST,
                resMsg,
                null
        );
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ChatApiResponse<Object> handleUnexpectedTypeException(UnexpectedTypeException e) {
        return ChatApiResponse.of(
                HttpStatus.BAD_REQUEST,
                "검증 조건이 잘못 설정되어 있습니다. 서버 로그를 확인해주세요.",
                null
        );
    }
}
