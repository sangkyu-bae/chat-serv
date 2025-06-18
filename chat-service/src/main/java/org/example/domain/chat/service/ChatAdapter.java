package org.example.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.chatroom.service.ChatRoomService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAdapter {

    private final ChatRoomService chatRoomService;

    public Mono<Void> test(String name){
//        chatRoomService.createSingleRoom(name);

        return Mono.empty();
    }
}
