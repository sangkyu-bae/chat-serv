package org.example.domain.chat.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.domain.chat.domain.ChatMessage;
import org.example.modules.converter.JsonConverter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ChatSendListener {

    private final JsonConverter jsonConverter;

    @KafkaListener(
                topicPartitions = @TopicPartition(
                        topic = "${kafka.topic.chat}",
                        partitions = { "${kafka.server.id}" }
                )
                , groupId = "${kafka.group.chat}"
            )
    public void chatListener(String chatMessage){
        ChatMessage message = null;

        try{
            message = jsonConverter.toObject(chatMessage, ChatMessage.class);
        }catch (Exception e){

        }
    }
}
