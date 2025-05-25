package org.example.infra.error;

import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.example.infra.ErrorCode;
import org.example.infra.ErrorException;
import org.example.infra.api.ChatApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

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
        return ChatApiResponse.of(
                    HttpStatus.BAD_REQUEST,
                    e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                        null
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ChatApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ChatApiResponse.of(
                HttpStatus.BAD_REQUEST,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
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
