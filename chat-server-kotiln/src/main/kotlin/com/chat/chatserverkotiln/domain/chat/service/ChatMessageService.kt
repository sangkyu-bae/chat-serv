package com.chat.chatserverkotiln.domain.chat.service

import com.chat.chatserverkotiln.module.redis.RedisRepository
import com.chat.chatserverkotiln.module.websocket.WebSocketSessionManager
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ChatMessageService(
    private val subscriber: ChatSubscriber,
    private val redisRepository: RedisRepository,
    private val webSocketSessionManager: WebSocketSessionManager,
    @Value("\${chat.server-id}") private val serverId : String
) {

    companion object {
        private const val ROOM_KEY_PREFIX = "chat:room:join-user"
        private const val SESSION_KEY_PREFIX = "connected:session:"
        private const val SERVER_SEND_CONCURRENCY = 8
    }

    private val log = LoggerFactory.getLogger(ChatMessageService::class.java)
    private var subscription: Disposable? = null

    @PostConstruct
    fun start() {
        val serverKey = "$SESSION_KEY_PREFIX$serverId"

        // 무한 구독 시작
        subscriber.subscribe()
            .onBackpressureBuffer(10_000)
            .flatMap({ msg ->
                val roomKey = "$ROOM_KEY_PREFIX:${msg.roomId}"

                // 서버 유저만 먼저 Mono<List<String>>로 조회
                redisRepository.getAllSetMembers(serverKey)
                    .collectList()
                    .flatMapMany { serverUsers ->
                        val serverSet = serverUsers.toSet()

                        // roomUsers는 collect 안 하고 바로 streaming
                        redisRepository.getAllSetMembers(roomKey)
                            .filter { userId -> serverSet.contains(userId) }
                            .flatMap({ userId ->
                                webSocketSessionManager.sendToUser(userId, msg.content)
                            }, 32)
                    }
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