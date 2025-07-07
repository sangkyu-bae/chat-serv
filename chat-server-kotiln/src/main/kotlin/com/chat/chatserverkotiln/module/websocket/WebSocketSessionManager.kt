//package com.chat.chatserverkotiln.module.websocket
//
//import org.springframework.stereotype.Component
//import org.springframework.web.reactive.socket.WebSocketSession
//import java.util.concurrent.ConcurrentHashMap
//
//@Component
//class WebSocketSessionManager {
//    private val sessions = ConcurrentHashMap<String, WebSocketSession>()
//
//    fun addSession(session: WebSocketSession) {
//        sessions[session.id] = session
//    }
//
//    fun removeSession(session: WebSocketSession) {
//        sessions.remove(session.id)
//    }
//
//    fun getSessions(): Collection<WebSocketSession> = sessions.values
//}



package com.chat.chatserverkotiln.module.websocket

import com.chat.chatserverkotiln.module.redis.RedisRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketSessionManager(
    private val redisRepository: RedisRepository
) {
    private val localSessions = ConcurrentHashMap<String, WebSocketSession>()
    
    companion object {
        private const val SESSION_KEY_PREFIX = "connected:session:"
        private const val USER_SESSION_KEY_PREFIX = "connected:user:"
        private const val SERVER_KEY_PREFIX = "server:user:"
    }

    fun addSession(session: WebSocketSession, userId: String? = null): Mono<Void> {
        localSessions[session.id] = session
        
        val ip = session.handshakeInfo.remoteAddress?.address?.hostAddress ?: "unknown"
        val port = session.handshakeInfo.remoteAddress?.port ?: 0
        val sessionKey = "$SESSION_KEY_PREFIX${session.id}"
        val server = "$ip:$port"
        val serverKey = "$SERVER_KEY_PREFIX$server"
        
        // userId가 null이 아닐 때만 사용자 관련 저장
        return if (userId != null) {
            val userKey = "$USER_SESSION_KEY_PREFIX$userId"
            
            val saveUser = mapOf(
                "sessionId" to session.id,
                "isConnect" to "true",
                "server" to server
            )

            val saveSession = mapOf(
                "userId" to userId,
                "isConnect" to "true",
                "server" to server
            )
            
            Mono.`when`(
                // suspend 함수를 Mono로 변환
                Mono.fromCallable { redisRepository.saveWithHashByWebFlux(sessionKey, saveSession) },
                Mono.fromCallable { redisRepository.saveWithHashByWebFlux(userKey, saveUser) },
                Mono.fromCallable { redisRepository.setTypeSaveByWebFlux(serverKey, userId) }
            ).then()
        } else {
            // userId가 null인 경우 세션만 저장
            val saveSession = mapOf(
                "userId" to "anonymous",
                "isConnect" to "true",
                "server" to server
            )
            
            Mono.fromCallable { 
                redisRepository.saveWithHashByWebFlux(sessionKey, saveSession)
            }.then()
        }
    }

    fun removeSession(session: WebSocketSession, userId: String? = null): Mono<Void> {
        localSessions.remove(session.id)
        
        val sessionKey = "$SESSION_KEY_PREFIX${session.id}"
        
        return if (userId != null) {
            val userKey = "$USER_SESSION_KEY_PREFIX$userId"
            val ip = session.handshakeInfo.remoteAddress?.address?.hostAddress ?: "unknown"
            val port = session.handshakeInfo.remoteAddress?.port ?: 0
            val server = "$ip:$port"
            val serverKey = "$SERVER_KEY_PREFIX$server"
            
            Mono.`when`(
                // Redis에서 데이터 삭제 (RedisRepository에 delete 메서드 추가 필요)
                Mono.fromCallable { redisRepository.deleteHash(sessionKey) },
                Mono.fromCallable { redisRepository.deleteHash(userKey) },
                Mono.fromCallable { redisRepository.removeFromSet(serverKey, userId) }
            ).then()
        } else {
            Mono.fromCallable { 
                redisRepository.deleteHash(sessionKey) 
            }.then()
        }
    }

    fun getLocalSessions(): Collection<WebSocketSession> = localSessions.values
    
    fun getSessionById(sessionId: String): WebSocketSession? = localSessions[sessionId]
    
    fun getUserSession(userId: String): Mono<String?> {
        val userKey = "$USER_SESSION_KEY_PREFIX$userId"
        return Mono.fromCallable { 
            redisRepository.findByHash(userKey, "sessionId") 
        }.flatMap { it }
    }
    
    fun isSessionActive(sessionId: String): Boolean = localSessions.containsKey(sessionId)
    
    fun getActiveSessionCount(): Mono<Long> {
        // Redis에서 세션 키 개수 조회 (RedisRepository에 메서드 추가 필요)
        return Mono.fromCallable { 
            redisRepository.countKeys("$SESSION_KEY_PREFIX*") 
        }.flatMap { it }
    }
}
