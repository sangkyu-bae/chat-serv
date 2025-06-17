package org.example.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.ChatMessage;
import org.example.domain.chat.domain.SingleChatRoom;
import org.example.domain.chat.service.ChatRoomService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatRoomStompController {

    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat/single-room/{roomId}")
    public Mono<Void> handleMessage(@DestinationVariable String roomId, SingleChatRoom singleChatRoom) {
        return chatRoomService.createSingleRoom(singleChatRoom)
                .doOnSuccess(result -> {
                    log.info("채팅방 생성 성공: {}", roomId);
                })
                .doOnError(error -> {
                    log.error("채팅방 생성 실패: {}", error.getMessage());
                })
                .then();
    }
}
