//package com.chat.chatserverkotiln.module.websocket
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.springframework.data.redis.core.ReactiveRedisTemplate
//import org.springframework.stereotype.Service
//import org.springframework.web.reactive.socket.WebSocketMessage
//import org.springframework.web.reactive.socket.WebSocketSession
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import java.time.Duration
//
//@Service
//class MessageBroadcastService(
//    private val sessionManager: WebSocketSessionManager,
//    private val redisTemplate: ReactiveRedisTemplate<String, String>,
//    private val objectMapper: ObjectMapper
//) {
//
//    companion object {
//        private const val ROOM_USERS_KEY_PREFIX = "chat:room:users:"
//        private const val MESSAGE_HISTORY_KEY_PREFIX = "chat:room:messages:"
//    }
//
//    /**
//     * 특정 방의 모든 사용자에게 메시지 브로드캐스팅
//     */
//    fun broadcastToRoom(roomId: String, message: ChatMessage): Mono<Void> {
//        return getRoomUsers(roomId)
//            .flatMapMany { userIds ->
//                Flux.fromIterable(userIds)
//                    .flatMap { userId ->
//                        sessionManager.getUserSession(userId)
//                            .filter { sessionId -> sessionId != null }
//                            .map { sessionId -> sessionId!! }
//                            .flatMap { sessionId ->
//                                val session = sessionManager.getSessionById(sessionId)
//                                if (session != null && session.isOpen) {
//                                    sendMessageToSession(session, message)
//                                } else {
//                                    // 세션이 닫혀있으면 Redis에서 제거
//                                    sessionManager.removeSession(session, userId)
//                                    Mono.empty()
//                                }
//                            }
//                    }
//            }
//            .then()
//            .then(saveMessageToHistory(roomId, message))
//    }
//
//    /**
//     * 특정 사용자에게 메시지 전송
//     */
//    fun sendToUser(userId: String, message: ChatMessage): Mono<Void> {
//        return sessionManager.getUserSession(userId)
//            .filter { sessionId -> sessionId != null }
//            .map { sessionId -> sessionId!! }
//            .flatMap { sessionId ->
//                val session = sessionManager.getSessionById(sessionId)
//                if (session != null && session.isOpen) {
//                    sendMessageToSession(session, message)
//                } else {
//                    sessionManager.removeSession(session, userId)
//                    Mono.empty()
//                }
//            }
//            .then()
//    }
//
//    /**
//     * 방에 사용자 추가
//     */
//    fun addUserToRoom(roomId: String, userId: String): Mono<Void> {
//        return redisTemplate.opsForSet().add("$ROOM_USERS_KEY_PREFIX$roomId", userId)
//            .then()
//    }
//
//    /**
//     * 방에서 사용자 제거
//     */
//    fun removeUserFromRoom(roomId: String, userId: String): Mono<Void> {
//        return redisTemplate.opsForSet().remove("$ROOM_USERS_KEY_PREFIX$roomId", userId)
//            .then()
//    }
//
//    /**
//     * 방의 사용자 목록 조회
//     */
//    fun getRoomUsers(roomId: String): Mono<Set<String>> {
//        return redisTemplate.opsForSet().members("$ROOM_USERS_KEY_PREFIX$roomId")
//            .collectList()
//            .map { it.toSet() }
//    }
//
//    private fun sendMessageToSession(session: WebSocketSession, message: ChatMessage): Mono<Void> {
//        return try {
//            val messageJson = objectMapper.writeValueAsString(message)
//            session.send(Mono.just(session.textMessage(messageJson)))
//        } catch (e: Exception) {
//            Mono.error(e)
//        }
//    }
//
//    private fun saveMessageToHistory(roomId: String, message: ChatMessage): Mono<Void> {
//        return try {
//            val messageJson = objectMapper.writeValueAsString(message)
//            redisTemplate.opsForList()
//                .leftPush("$MESSAGE_HISTORY_KEY_PREFIX$roomId", messageJson)
//                .then(redisTemplate.expire("$MESSAGE_HISTORY_KEY_PREFIX$roomId", Duration.ofDays(30)))
//                .then()
//        } catch (e: Exception) {
//            Mono.error(e)
//        }
//    }
//}
//
//data class ChatMessage(
//    val type: String, // "CHAT", "JOIN", "LEAVE", "SYSTEM"
//    val roomId: String,
//    val userId: String?,
//    val content: String,
//    val timestamp: Long = System.currentTimeMillis()
//)