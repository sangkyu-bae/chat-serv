package com.chat.chatserverkotiln.domain.chat.service

import com.chat.chatserverkotiln.domain.chat.domain.ChatMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Service
import reactor.core.publisher.BufferOverflowStrategy
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import reactor.util.retry.Retry


@Service
class ChatSubscriber (
    private val container : ReactiveRedisMessageListenerContainer,
    @Value("\${chat.server-id}") private val serverId : String
        ){

    private val mapper = jacksonObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)
    fun subscribe(): Flux<ChatMessage>{
//        val topic = ChannelTopic("chat:server:$serverId")
        val topic = ChannelTopic("chat:server:1")

        return container.receive(topic)
            // ⬇️ 원시 Redis 이벤트 로그 (채널/페이로드)
            .doOnSubscribe { log.info("SUBSCRIBE start channel='{}'", topic.topic) }
            .doOnNext { raw -> log.info("RECV raw ch='{}' msg='{}'", raw.channel, raw.message) }
            .map { it.message }
            .flatMap { json ->
                Mono.fromCallable { mapper.readValue<ChatMessage>(json) }
                    .onErrorResume { e ->
                        log.warn("JSON 파싱 실패: ${e.message} payload=$json")
                        Mono.empty()
                    }
            }
            .onBackpressureBuffer(
                10_000,
                { dropped -> log.warn("백프레셔로 드롭: $dropped") },
                BufferOverflowStrategy.DROP_OLDEST
            )
            .retryWhen(
                Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1))
                    .maxBackoff(Duration.ofSeconds(30))
                    .transientErrors(true)
            )
            .publish()
            .refCount(1)
    }

}