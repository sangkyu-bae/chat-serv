package org.example.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.domain.ChatRoom;

@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestChatRoom {

    private String roomKey;
    private String createAt;
    private String updateAt;
    private String isDeleted;
    private String lastSendMsg;
    private String lastSendUser;

    public static RequestChatRoom from(ChatRoom room) {
        return RequestChatRoom.builder()
                .roomKey(room.getRoomKey())
                .createAt(room.getCreateAt().toString())
                .updateAt(room.getUpdateAt().toString())
                .isDeleted(String.valueOf(room.isDeleted()))
                .lastSendMsg(room.getLastSendMsg())
                .lastSendUser(room.getLastSendUser())
                .build();
    }

}
