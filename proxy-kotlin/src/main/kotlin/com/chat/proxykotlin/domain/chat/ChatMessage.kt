package com.chat.proxykotlin.domain.chat

data class ChatMessage(
    val type: MessageType,
    val roomId: String,
    val userId: String?,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
enum class MessageType {
    CHAT,
    JOIN,
    LEAVE,
    SYSTEM
}