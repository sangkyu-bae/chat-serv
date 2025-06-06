package org.example.domain.chat.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.JsonConverter;
import org.example.domain.ChatMessage;
import org.example.module.EurekaSendManager;
import org.example.module.RedisRepository;
import org.example.module.redis.RedisKeyManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatListener {


    private final JsonConverter jsonConverter;

    private final EurekaSendManager eurekaSendManager;
    private final RedisRepository redisRepository;

    private final RedisKeyManager redisKeyManager;

    @KafkaListener(topics ="${kafka.topic.chat}",groupId = "${kafka.group.chat}")
    public void resultTaskListener(String chatMessage){
        ChatMessage message = null;
        try{
            message = jsonConverter.toObject(chatMessage, ChatMessage.class);

            String roomKey = redisKeyManager.getRoomKey(message.getRoomId());
            List<Object> list = redisRepository.findWithListTypeByAll(roomKey);

            List<String> sendUserList = list.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());

            List<String> users = sendUserList.stream()
                    .map(user -> redisKeyManager.getUserKey(user))
                    .collect(Collectors.toList());

            List<String> serverInfo = eurekaSendManager.getServiceInstances("chat-service");
            List<String> server = new ArrayList<>();
            for(String user : users){
                Object userSession = redisRepository.findByHash(user,"server");

                if(!serverInfo.contains(userSession.toString())){
                    continue;
                }

                server.add(userSession.toString());
            }

            //send chat
            //save chat 가공 및 응답

        } catch (Exception e){

        } finally {

        }
    }
}
