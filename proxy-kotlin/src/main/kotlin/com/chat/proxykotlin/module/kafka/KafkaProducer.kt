package com.chat.proxykotlin.module.kafka

import com.chat.proxykotlin.domain.chat.ChatMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import kotlin.coroutines.suspendCoroutine

@Component
class KafkaProducer(
    private val kafkaTemplate :KafkaTemplate<String,String>,
    private val objectMapper: ObjectMapper
) {

    fun send(topic: String, key: String, message: Any) {
        val json = objectMapper.writeValueAsString(message)
        kafkaTemplate.send(topic, key, json)
    }

    fun send(topic: String, message: Any) {
        val json = objectMapper.writeValueAsString(message)
        kafkaTemplate.send(topic, json)
    }
}