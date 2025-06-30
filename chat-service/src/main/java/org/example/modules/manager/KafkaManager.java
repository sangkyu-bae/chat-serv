package org.example.modules.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaManager {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;
    public Mono<Void> sendByAsync(String topic, Object sendMsg){
        try {
            String send = objectMapper.writeValueAsString(sendMsg);
            kafkaTemplate.send(topic,send);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Mono.empty();
    }


    public Mono<Void> sendByAsync(String topic,String key , Object sendMsg){
        try {
            String send = objectMapper.writeValueAsString(sendMsg);
            kafkaTemplate.send(topic,send,key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Mono.empty();
    }


    public void sendBySync(String topic, String key, Object sendMsg){
        try{
            String send = objectMapper.writeValueAsString(sendMsg);
            kafkaTemplate.send(topic,send,key);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }


}
