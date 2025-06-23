package org.example.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.module.converter.DateTimeConverter;
import org.example.module.converter.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom {

    private String roomKey;

    private List<String> joinList;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private boolean isDeleted;

    private String lastSendMsg;

    private String lastSendUser;

    public static ChatRoom create(List<String> joinList, Map<String, String> redisHash, String roomKey){

        LocalDateTime createAt = DateTimeConverter.toLocalDate(redisHash.getOrDefault("createAt",""));
        LocalDateTime updateAt = DateTimeConverter.toLocalDate(redisHash.getOrDefault("createAt",""));
        boolean isDelete = TypeConverter.toValueByBoolean(redisHash.getOrDefault("isDeleted","false"));
        String lastSendMsg = redisHash.getOrDefault("lastSendMsg","");
        String lastSendUser = redisHash.getOrDefault("lastSendUser","");

        return new ChatRoom(
                roomKey,
                joinList,
                createAt,
                updateAt,
                isDelete,
                lastSendMsg,
                lastSendUser
        );
    }

    public static ChatRoom create(List<String> joinUserList){
        String roomKey = String.valueOf(UUID.randomUUID());
        return new ChatRoom(
                roomKey,
                joinUserList,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                "",
                ""
        );
    }
}
