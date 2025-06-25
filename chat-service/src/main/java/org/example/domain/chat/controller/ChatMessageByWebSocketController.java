package org.example.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.ChatMessage;
import org.example.domain.chat.domain.SingleChatRoom;
import org.example.domain.chat.service.ChatMessageService;
import org.example.domain.chatroom.service.ChatRoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatMessageByWebSocketController {
    private final ChatMessageService chatMessageService;

    private final ChatRoomService chatRoomService;

    @MessageMapping("/send/msg")
    public String sendMessage(ChatMessage chatMessage) {

        try{
            chatMessageService.sendMessageBySingleRoom(chatMessage);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "ok";
    }

}
