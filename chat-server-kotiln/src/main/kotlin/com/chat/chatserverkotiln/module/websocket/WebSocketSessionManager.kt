package com.chat.chatserverkotiln.module.websocket

import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketSessionManager {
    private val sessions = ConcurrentHashMap<String, WebSocketSession>()

    fun addSession(session: WebSocketSession) {
        sessions[session.id] = session
    }

    fun removeSession(session: WebSocketSession) {
        sessions.remove(session.id)
    }

    fun getSessions(): Collection<WebSocketSession> = sessions.values
}
