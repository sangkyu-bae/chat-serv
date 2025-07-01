package com.chat.chatserverkotiln.module.websocket

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class EchoWebSocketHandler(
    private val sessionManager: WebSocketSessionManager
) : WebSocketHandler {
    private val log = LoggerFactory.getLogger(EchoWebSocketHandler::class.java)

    override fun handle(session: WebSocketSession): Mono<Void> {
        sessionManager.addSession(session)
        log.info("🔌 Connected: ${session.id}")

        val input = session.receive()
            .flatMap { webSocketMessage ->
                val msgText = webSocketMessage.payloadAsText
                log.info("" Received from ${session.id}: $msgText")
                val sendMonos: List<Mono<Void>> = sessionManager.getSessions()
                    .map { s -> s.send(Mono.just(s.textMessage("Broadcast: $msgText"))) }
                Mono.`when`(sendMonos)
            }
            .doOnComplete {
                log.info(" Disconnected (complete): ${session.id}")
            }
            .doOnError { e ->
                log.error(" Error in session ${session.id}", e)
            }

        return input
            .then()
            .doFinally {
                sessionManager.removeSession(session)
                log.info("️ Session removed: ${session.id}")
            }
    }
}