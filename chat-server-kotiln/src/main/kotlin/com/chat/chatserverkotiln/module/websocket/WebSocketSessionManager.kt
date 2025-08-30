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
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import org.springframework.beans.factory.annotation.Value

@Component
class WebSocketSessionManager(
    private val redisRepository: RedisRepository
) {
    private val localSessions = ConcurrentHashMap<String, WebSocketSession>()
    private val log = LoggerFactory.getLogger(WebSocketSessionManager::class.java)
    companion object {
        private const val SESSION_KEY_PREFIX = "connected:session:"
        private const val USER_SESSION_KEY_PREFIX = "connected:user:"
        private const val SERVER_KEY_PREFIX = "server:user:"
    }

//    fun getSessions(): Collection<WebSocketSession> = localSessions.values
    fun getSessions(): Collection<WebSocketSession> = localSessions.values
    fun addSession(session: WebSocketSession, userId: String): Mono<Void> {
//        localSessions[session.id] = session//snik 방식으로 변경 필요
        localSessions[userId] = session//snik 방식으로 변경 필요

        val ip = session.handshakeInfo.remoteAddress?.address?.hostAddress ?: "unknown"
        val port = session.handshakeInfo.remoteAddress?.port ?: 0
        val sessionKey = "$SESSION_KEY_PREFIX${session.id}"
        val server = "$ip:$port"
        val serverKey = "$SERVER_KEY_PREFIX$server"

        log.info("WebSocket 연결: sessionId={}, ip={}, port={}, userId={}", session.id, ip, port, userId)

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

            log.debug("Redis 저장 - sessionKey={}, data={}", sessionKey, saveSession)
            log.debug("Redis 저장 - userKey={}, data={}", userKey, saveUser)
            log.debug("Redis 저장 - serverKey={}, value={}", serverKey, userId)

            Mono.`when`(
//                redisRepository.saveWithHashByWebFlux(sessionKey, saveSession),
//                redisRepository.saveWithHashByWebFlux(userKey, saveUser),
//                redisRepository.setTypeSaveByWebFlux(serverKey, userId)
            ).doOnSuccess {
                log.info("Redis 저장 성공: sessionId={}, userId={}", session.id, userId)
            }.doOnError { e ->
                log.error("Redis 저장 실패: sessionId={}, userId={}, error={}", session.id, userId, e.message)
            }.then()
        } else {
            val saveSession = mapOf(
                "userId" to "anonymous",
                "isConnect" to "true",
                "server" to server
            )

            log.debug("Redis 저장 - sessionKey={}, data={}", sessionKey, saveSession)

            redisRepository.saveWithHashByWebFlux(sessionKey, saveSession)
                .doOnSuccess {
                    log.info("Redis 저장 성공 (anonymous): sessionId={}", session.id)
                }.doOnError { e ->
                    log.error("Redis 저장 실패 (anonymous): sessionId={}, error={}", session.id, e.message)
                }.then()
        }
    }
    fun removeSession(session: WebSocketSession, userId: String? = null): Mono<Void> {
        localSessions.remove(session.id)
        
        val sessionKey = "$SESSION_KEY_PREFIX${session.id}"
        log.debug("Redis 삭제 - sessionKey={}", sessionKey)

        return if (userId != null) {
            val userKey = "$USER_SESSION_KEY_PREFIX$userId"
            val ip = session.handshakeInfo.remoteAddress?.address?.hostAddress ?: "unknown"
            val port = session.handshakeInfo.remoteAddress?.port ?: 0
            val server = "$ip:$port"
            val serverKey = "$SERVER_KEY_PREFIX$server"
            
            Mono.`when`(
                // Redis에서 데이터 삭제 (RedisRepository에 delete 메서드 추가 필요)
//                redisRepository.deleteHashByWebFlux(sessionKey),
//                redisRepository.deleteHashByWebFlux(userKey),
//                redisRepository.removeSetByWebflux(serverKey, userId)


//                Mono.fromCallable { redisRepository.deleteHashByWebFlux(sessionKey) },
//                Mono.fromCallable { redisRepository.deleteHashByWebFlux(userKey) },
//                Mono.fromCallable { redisRepository.removeSetByWebflux(serverKey, userId) }
            ).doOnSuccess {
                log.info("Redis 삭제 성공: sessionId={}, userId={}", session.id, userId)
            }.doOnError { e ->
                log.error("Redis 삭제 실패: sessionId={}, userId={}, error={}", session.id, userId, e.message)
            }.
            then()
        } else {
            redisRepository.deleteHashByWebFlux(sessionKey)
                .doOnSuccess {
                    log.info("Redis 삭제 성공 (anonymous): sessionId={}", session.id)
                }.doOnError { e ->
                    log.error("Redis 삭제 실패 (anonymous): sessionId={}, error={}", session.id, e.message)
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
        return  redisRepository.countKeysByWebflux("$SESSION_KEY_PREFIX*")
    }

    /** 외부에서 호출: 특정 유저에게 텍스트 전송 (논블로킹) */
    fun sendToUser(userId: String, text: String): Mono<Void> =
        Mono.defer {
            val d =getSessions()
            log.info("active ws sessions={}", d.size)
            val session = localSessions[userId]
                ?: return@defer Mono.error(IllegalStateException("No session for $userId"))

            // 한 번의 send로 반환된 Mono를 그대로 리턴 (구독은 호출자/프레임워크가 함)
            session.send(Mono.just(session.textMessage(text)))
        }

}
