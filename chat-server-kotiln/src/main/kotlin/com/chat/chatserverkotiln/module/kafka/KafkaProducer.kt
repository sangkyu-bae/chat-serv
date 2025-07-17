package com.chat.chatserverkotiln.module.kafka

import com.chat.chatserverkotiln.module.websocket.EchoWebSocketHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import java.util.Objects

@Component
class KafkaProducer (
    private val kafkaSender: KafkaSender<String,String>,
    private val objectMapper: ObjectMapper // ObjectMapper 주입
        ){
    private val log = LoggerFactory.getLogger(KafkaProducer::class.java)


    fun sendMessage(topic: String, key: String, sendMsg: Any): Mono<SenderResult<Void?>> {
        val jsonMessage = objectMapper.writeValueAsString(sendMsg)

        val record = SenderRecord.create(topic, null, null, key, jsonMessage, null as Void?)

        return kafkaSender.send(Mono.just(record))
            .doOnNext { result ->
                log.info("Kafka message sent: topic=${result.recordMetadata().topic()}, offset=${result.recordMetadata().offset()}")
            }
            .single()
    }
}