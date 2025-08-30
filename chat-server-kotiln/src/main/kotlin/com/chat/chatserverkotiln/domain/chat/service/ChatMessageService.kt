package com.chat.chatserverkotiln.domain.chat.service

import com.chat.chatserverkotiln.module.redis.RedisRepository
import com.chat.chatserverkotiln.module.websocket.WebSocketSessionManager
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.Disposable

@Component
class ChatMessageService(
    private val subscriber: ChatSubscriber,
    private val redisRepository: RedisRepository,
    private val webSocketSessionManager: WebSocketSessionManager
) {

    companion object {
        private const val ROOM_KEY_PREFIX = "chat:room:join-user"
        private const val SERVER_SEND_CONCURRENCY = 8
    }

    private val log = LoggerFactory.getLogger(ChatMessageService::class.java)
    private var subscription: Disposable? = null

    @PostConstruct
    fun start() {
        // 무한 구독 시작
        subscriber.subscribe()
            .onBackpressureBuffer(10_000)
            .flatMap({ msg ->
                val key = "$ROOM_KEY_PREFIX:${msg.roomId}"
                log.info("key : {}",key);
                redisRepository.getAllSetMembers(key)      // Flux<String>
                    .flatMap({ userId ->

                        webSocketSessionManager.sendToUser(userId, msg.content) // Mono<Void>
                    }, 32)
                    .then() // Mono<Void>
            }, 64)
            .onErrorContinue { e, v -> log.warn("fanout 실패: value=$v, err=${e.message}") }
            .subscribe()
    }

    @PreDestroy
    fun stop() {
        subscription?.dispose()
        subscription = null
        log.info("🛑 Fanout subscription disposed")
    }
}