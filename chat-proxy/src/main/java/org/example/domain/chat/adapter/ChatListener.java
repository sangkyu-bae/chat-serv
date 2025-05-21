package org.example.domain.chat.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.JsonConverter;
import org.example.domain.ChatMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatListener {


    private final JsonConverter jsonConverter;

    @KafkaListener(topics ="${kafka.topic.chat}",groupId = "${kafka.group.chat}")
    public void resultTaskListener(String chatMessage){
        ChatMessage message = null;
        try{
            message = jsonConverter.toObject(chatMessage, ChatMessage.class);
        } catch (Exception e){

        } finally {

        }
    }
}
