package com.chat.user.infra.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChatApiResponse<T> {

    private int code;

    private HttpStatus status;

    private String message;

    private T data;

    public ChatApiResponse(HttpStatus status, String message, T data){
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }
    public static <T> ChatApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new ChatApiResponse<>(httpStatus, message, data);
    }

    public static <T> ChatApiResponse<T> of(HttpStatus httpStatus, T data) {
        return of(httpStatus, httpStatus.name(), data);
    }


    public static <T> ChatApiResponse <T> ok(T data){
        return of(HttpStatus.OK,data);
    }
}
