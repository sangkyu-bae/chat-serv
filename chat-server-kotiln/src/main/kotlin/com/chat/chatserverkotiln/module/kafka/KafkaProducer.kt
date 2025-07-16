package com.chat.chatserverkotiln.module.kafka

import org.springframework.stereotype.Component

@Component
class KafkaProducer (
    private val kafkaProducer: KafkaProducer
        ){
}