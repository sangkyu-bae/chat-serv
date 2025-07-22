package com.chat.proxykotlin.domain.chat

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ChatListener(
    private val objectMapper: ObjectMapper,
) {
}