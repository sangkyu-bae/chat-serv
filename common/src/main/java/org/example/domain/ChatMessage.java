package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private String toUser;
} 