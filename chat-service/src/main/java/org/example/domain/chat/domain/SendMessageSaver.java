package org.example.domain.chat.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SendMessageSaver {

    public enum StatusType {
        PENDING,SUCCESS,FAIL
    }

    private StatusType status;

    private String message;

    private String sendAt;


    public static SendMessageSaver createGenerate(StatusType statusType,String message, String sendAt){
        return new SendMessageSaver(statusType,message,sendAt);
    }

}
