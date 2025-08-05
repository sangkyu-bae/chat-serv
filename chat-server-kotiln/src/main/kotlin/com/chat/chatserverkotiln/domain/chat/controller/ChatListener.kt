package com.chat.chatserverkotiln.domain.chat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver

@Component
class ChatListener (
    private val objectMapper: ObjectMapper,
    private val kafkaReceiver: KafkaReceiver<String, String>
        ){
    private val log = LoggerFactory.getLogger(ChatListener::class.java)

    @PostConstruct
    fun consumeMessages() {
        kafkaReceiver
            .receive()
            .doOnNext { record ->
                val key = record.key()
                val value = record.value()
                println("Received message: key=$key, value=$value")

                // 수동 커밋이 필요한 경우
//                record.receiverOffset().acknowledge()
            }
            .subscribe()
    }
}