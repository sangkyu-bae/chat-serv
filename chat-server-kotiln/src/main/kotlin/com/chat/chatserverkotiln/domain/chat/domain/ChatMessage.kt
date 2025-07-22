package com.chat.chatserverkotiln.domain.chat.domain

data class ChatMessage(
    val type: MessageType, // "CHAT", "JOIN", "LEAVE", "SYSTEM"
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