package com.chat.chatserverkotiln.module.websocket

import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class EchoWebSocketHandler : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.receive()
            .doOnNext{println("Recevied: $it")}
            .map { session.textMessage("Echo : $it") }
            .let { session.send(it) }
    }
}