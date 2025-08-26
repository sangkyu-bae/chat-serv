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
        val topic = ChannelTopic("chat:server:$serverId")

        return container.receive(topic)                 // Flux<Message<String, String>>
            .map { it.message }                         // JSON 문자열
            .flatMap { json ->
                Mono.fromCallable { mapper.readValue<ChatMessage>(json) }
                    .onErrorResume { e ->
                        log.warn("JSON 파싱 실패: ${e.message} payload=$json")
                        Mono.empty()
                    }
            }
            // 순간 폭주 방어(용량/정책은 상황 맞게 조정)
            .onBackpressureBuffer(
                10_000,
                { dropped -> log.warn("백프레셔로 드롭: $dropped") },
                BufferOverflowStrategy.DROP_OLDEST
            )
            // 연결/네트워크 오류로 끊겨도 자동 재시도
            .retryWhen(
                Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1))
                    .maxBackoff(Duration.ofSeconds(30))
                    .transientErrors(true)
            )
            // 여러 소비자가 붙어도 하나의 구독만 유지(옵션)
            .publish()
            .refCount(1)

    }

}