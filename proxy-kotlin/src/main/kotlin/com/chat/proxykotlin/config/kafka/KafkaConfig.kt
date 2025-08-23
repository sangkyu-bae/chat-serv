package com.chat.proxykotlin.config.kafka

import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.*

@Configuration
class KafkaConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var KAFKA_BOOTSTRAP_SERVER: String

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val config: Map<String, Any> = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to KAFKA_BOOTSTRAP_SERVER,
//            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:909222324",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
        )
        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val config: Map<String, Any> = mapOf(
//            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:909222324",
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to KAFKA_BOOTSTRAP_SERVER,
            ConsumerConfig.GROUP_ID_CONFIG to "my-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )
        return DefaultKafkaConsumerFactory(config)
    }
}