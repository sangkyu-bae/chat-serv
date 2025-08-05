package com.chat.chatserverkotiln.module.kafka

//import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfig {
    @Bean
    fun kafkaSender(): KafkaSender<String, String> {
        val props = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to org.apache.kafka.common.serialization.StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to org.apache.kafka.common.serialization.StringSerializer::class.java,
            ProducerConfig.ACKS_CONFIG to "1"
        )

        val senderOptions = SenderOptions.create<String, String>(props)
        return KafkaSender.create(senderOptions)
    }


    @Bean
    fun kafkaReceiver(): KafkaReceiver<String, String> {
        val props: Map<String, Any> = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "chat-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false // 수동 커밋 시 사용
        )

        val topics = listOf("chat-proxy")
        val receiverOptions = ReceiverOptions.create<String, String>(props)
            .subscription(topics)

        return KafkaReceiver.create(receiverOptions)
    }
}