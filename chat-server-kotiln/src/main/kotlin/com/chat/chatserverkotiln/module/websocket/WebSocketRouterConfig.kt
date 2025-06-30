package com.chat.chatserverkotiln.module.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketRouterConfig {
    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter = WebSocketHandlerAdapter()

    @Bean
    fun handlerMapping(echoHandler : EchoWebSocketHandler) : HandlerMapping{
        val map = mapOf("/ws/echo" to echoHandler)
        val order = -1
        return SimpleUrlHandlerMapping(map,order)
    }
}