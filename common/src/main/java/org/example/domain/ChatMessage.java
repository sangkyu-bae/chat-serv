package org.example.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    
    public enum MessageType {
        ENTER, TALK, LEAVE
    }
    
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private String timestamp;

    public String getPrefixMessage(){
        return this.sender + ":" + this.message;
    }

}