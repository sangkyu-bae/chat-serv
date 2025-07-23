package com.chat.proxykotlin.domain.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener

import org.springframework.stereotype.Component

@Component
class ChatListener(
    private val objectMapper: ObjectMapper,
) {

    private val log = LoggerFactory.getLogger(ChatListener::class.java)


    @KafkaListener(topics = ["\${spring.kafka.topic.chat}"], groupId = "1")
    fun chatConsume(message: String){
        log.info(message)

        val chatMessage = objectMapper.readValue(message,ChatMessage::class.java)
    }


}