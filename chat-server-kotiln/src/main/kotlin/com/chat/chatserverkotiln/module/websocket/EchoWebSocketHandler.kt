package com.chat.chatserverkotiln.module.websocket

import com.chat.chatserverkotiln.domain.chat.domain.ChatMessage
import com.chat.chatserverkotiln.domain.chat.domain.MessageType
import com.chat.chatserverkotiln.module.jwt.JwtTokenProvider
import com.chat.chatserverkotiln.module.kafka.KafkaProducer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI

@Component
class EchoWebSocketHandler(
    private val sessionManager: WebSocketSessionManager,
    private val kafkaProducer: KafkaProducer,
    private val objectMapper: ObjectMapper,
    private val jwtTokenProvider: JwtTokenProvider
) : WebSocketHandler {
    private val log = LoggerFactory.getLogger(EchoWebSocketHandler::class.java)

    override fun handle(session: WebSocketSession): Mono<Void> {
        // 1) 헤더에서만 토큰 추출
        val token = extractBearerTokenFromHeader(session)
        if (token == null) {
            log.warn("❌ Missing or invalid Authorization header, session=${session.id}")
            return session.close(CloseStatus.POLICY_VIOLATION) // 1008
        }
        if (!jwtTokenProvider.validateJwtToken(token)) {
            log.warn("❌ JWT validation failed, session=${session.id}")
            return session.close(CloseStatus.POLICY_VIOLATION)
        }

        // 2) 인증 통과 → 클레임 필요 시 사용
        val claims = jwtTokenProvider.getClaimsFromJwtToken(token)
        val userId = claims?.subject ?: "anonymous"
        log.info("🔐 Authenticated userId=$userId, session=${session.id}")

        // 3) 세션 등록 및 메시지 처리
        sessionManager.addSession(session,userId).subscribe()
        log.info("🔌 Connected: ${session.id}")

        val input = session.receive()
            .flatMap { webSocketMessage ->
                val msgText = webSocketMessage.payloadAsText
                log.info("📩 Received from ${session.id}: $msgText")
                val chatMessage: ChatMessage = objectMapper.readValue(msgText)

                mono {
                    // 필요 시 파티션 키/메시지에 userId 반영
                    kafkaProducer.sendMessage("chat", userId, chatMessage)
                }
            }
            .doOnComplete {
                sessionManager.removeSession(session).subscribe()
                log.info("🔌 Disconnected (complete): ${session.id}")
            }
            .doOnError { e ->
                log.error("⚠️ Error in session ${session.id}", e)
            }

        return input.then()
            .doFinally {
                sessionManager.removeSession(session).subscribe()
                log.info("♻️ Session removed: ${session.id}")
            }
    }

    private fun extractBearerTokenFromHeader(session: WebSocketSession): String? {
        session.handshakeInfo.headers.getFirst("Authorization")?.trim()?.let { raw ->
            val p = "Bearer "
            if (raw.startsWith(p, true) && raw.length > p.length) {
                return raw.substring(p.length).trim()
            }
        }
        // 2) 쿼리에서 token 또는 Authorization 키 허용
        val query = session.handshakeInfo.uri.query.orEmpty()
        log.info("🔌 query: ${query}")
        if (query.isNotBlank()) {
            val map = query.split("&").mapNotNull {
                val i = it.indexOf('=')
                if (i <= 0) null else it.substring(0, i) to it.substring(i + 1)
            }.toMap()
            map["token"]?.let { return it }
            map["Authorization"]?.let { return it.removePrefix("Bearer ").trim() }
        }
        return null
    }
}

//                val sendMonos: List<Mono<Void>> = sessionManager.getSessions()
//                    .map { s ->
//                        s.send(Mono.just(s.textMessage("Broadcast: $msgText")))
//                            .doOnSubscribe { log.info("🔈 Sending to ${s.id}") }
//                            .doOnSuccess {
//                                log.info("✅ Sent to ${s.id}")
//                            }
//                            .doOnError { e ->{
//                                sessionManager.removeSession(session)
//                                log.error("❌ Failed to send to ${s.id}", e)
//                            } }
//                    }
//                Mono.when(sendMonos)


//package com.chat.chatserverkotiln.module.websocket
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Component
//import org.springframework.web.reactive.socket.WebSocketHandler
//import org.springframework.web.reactive.socket.WebSocketSession
//import reactor.core.publisher.Mono
//
//@Component
//class EchoWebSocketHandler(
//    private val sessionManager: WebSocketSessionManager,
//    private val messageBroadcastService: MessageBroadcastService,
//    private val objectMapper: ObjectMapper
//) : WebSocketHandler {
//    private val log = LoggerFactory.getLogger(EchoWebSocketHandler::class.java)
//
//    override fun handle(session: WebSocketSession): Mono<Void> {
//        var currentUserId: String? = null
//        var currentRoomId: String? = null
//
//        sessionManager.addSession(session)
//        log.info("🔌 Connected: ${session.id}")
//
//        val input = session.receive()
//            .flatMap { webSocketMessage ->
//                val msgText = webSocketMessage.payloadAsText
//                log.info("📨 Received from ${session.id}: $msgText")
//
//                try {
//                    val message = objectMapper.readValue(msgText, ChatMessage::class.java)
//
//                    when (message.type) {
//                        "JOIN" -> {
//                            currentUserId = message.userId
//                            currentRoomId = message.roomId
//
//                            // 세션에 사용자 정보 저장
//                            sessionManager.addSession(session, currentUserId)
//                                .then(messageBroadcastService.addUserToRoom(currentRoomId!!, currentUserId!!))
//                                .then(messageBroadcastService.broadcastToRoom(
//                                    currentRoomId!!,
//                                    ChatMessage(
//                                        type = "SYSTEM",
//                                        roomId = currentRoomId!!,
//                                        userId = null,
//                                        content = "${currentUserId}님이 입장하셨습니다."
//                                    )
//                                ))
//                        }
//                        "LEAVE" -> {
//                            currentRoomId?.let { roomId ->
//                                currentUserId?.let { userId ->
//                                    messageBroadcastService.removeUserFromRoom(roomId, userId)
//                                        .then(messageBroadcastService.broadcastToRoom(
//                                            roomId,
//                                            ChatMessage(
//                                                type = "SYSTEM",
//                                                roomId = roomId,
//                                                userId = null,
//                                                content = "${userId}님이 퇴장하셨습니다."
//                                            )
//                                        ))
//                                        .then(sessionManager.removeSession(session, userId))
//                                }
//                            }
//                        }
//                        "CHAT" -> {
//                            currentRoomId?.let { roomId ->
//                                messageBroadcastService.broadcastToRoom(roomId, message)
//                            } ?: Mono.empty()
//                        }
//                        else -> {
//                            log.warn("Unknown message type: ${message.type}")
//                            Mono.empty()
//                        }
//                    }
//                } catch (e: Exception) {
//                    log.error("Failed to parse message: $msgText", e)
//                    // 에러 메시지를 클라이언트에게 전송
//                    session.send(Mono.just(session.textMessage(
//                        objectMapper.writeValueAsString(
//                            ChatMessage(
//                                type = "ERROR",
//                                roomId = "",
//                                userId = null,
//                                content = "메시지 형식이 올바르지 않습니다."
//                            )
//                        )
//                    )))
//                }
//            }
//            .doOnComplete {
//                log.info("🔌 Disconnected (complete): ${session.id}")
//                // 연결이 끊어지면 방에서 제거
//                currentRoomId?.let { roomId ->
//                    currentUserId?.let { userId ->
//                        messageBroadcastService.removeUserFromRoom(roomId, userId)
//                            .then(messageBroadcastService.broadcastToRoom(
//                                roomId,
//                                ChatMessage(
//                                    type = "SYSTEM",
//                                    roomId = roomId,
//                                    userId = null,
//                                    content = "${userId}님이 연결이 끊어졌습니다."
//                                )
//                            ))
//                            .subscribe()
//                    }
//                }
//            }
//            .doOnError { e ->
//                log.error("❌ Error in session ${session.id}", e)
//            }
//
//        return input
//            .then()
//            .doFinally {
//                currentUserId?.let { userId ->
//                    sessionManager.removeSession(session, userId)
//                } ?: sessionManager.removeSession(session)
//                log.info("🗑️ Session removed: ${session.id}")
//            }
//    }
//}