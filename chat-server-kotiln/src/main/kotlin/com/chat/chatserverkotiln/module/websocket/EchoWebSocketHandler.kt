package com.chat.chatserverkotiln.module.websocket

import com.chat.chatserverkotiln.module.kafka.KafkaProducer
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class EchoWebSocketHandler(
    private val sessionManager: WebSocketSessionManager,
    private val kafkaProducer: KafkaProducer
) : WebSocketHandler {
    private val log = LoggerFactory.getLogger(EchoWebSocketHandler::class.java)

    override fun handle(session: WebSocketSession): Mono<Void> {
        sessionManager.addSession(session).subscribe()
        log.info("🔌 Connected: ${session.id}")

        val input = session.receive()
            .flatMap { webSocketMessage ->
                val msgText = webSocketMessage.payloadAsText
                log.info(" Received from ${session.id}: $msgText")
                val test : ChatMessage = ChatMessage("CHAT","1","testUser",msgText)
                mono {
                    kafkaProducer.sendMessage("chat", "1", test)  // suspend 함수 호출
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
//                Mono.`when`(sendMonos)
            }
            .doOnComplete {
                sessionManager.removeSession(session).subscribe()
                log.info(" Disconnected (complete): ${session.id}")
            }
            .doOnError { e ->
                log.error(" Error in session ${session.id}", e)
            }

        return input
            .then()
            .doFinally {
                sessionManager.removeSession(session).subscribe()
                log.info("️ Session removed: ${session.id}")
            }
    }
}
data class ChatMessage(
    val type: String, // "CHAT", "JOIN", "LEAVE", "SYSTEM"
    val roomId: String,
    val userId: String?,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

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