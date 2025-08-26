package com.chat.chatserverkotiln.domain.chat.domain

data class SendMessage (
    val roomId: String,
    val message: String,
    val sendUser: String,
    val timestamp: Long
)