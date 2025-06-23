package org.example.domain.chat.domain;

import lombok.*;
import org.example.domain.ChatMessage;
import org.example.domain.chat.dto.RequestCreateRoom;
import org.example.module.converter.DateTimeConverter;

import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SingleChatRoom {

    private final String roomName;
    private final String toUser;
    private final String fromUser;

    public static SingleChatRoom create(RequestCreateRoom requestChatRoom) {

        return SingleChatRoom.builder()
                .toUser(requestChatRoom.getToUserId())
                .fromUser(requestChatRoom.getToUserId())
                .build();
    }

    public Map<String,String> getRoomByMap(){
        String time = DateTimeConverter.getToString14HHMI();

        return  Map.of(
                "isDeleted","false",
                "createAt",time
        ) ;
    }

    public static SingleChatRoom createGenerate(ChatMessage chatMessage){
        return SingleChatRoom.builder()
                .fromUser(chatMessage.getSender())
                .toUser(chatMessage.getToUser())
                .build();
    }


} 