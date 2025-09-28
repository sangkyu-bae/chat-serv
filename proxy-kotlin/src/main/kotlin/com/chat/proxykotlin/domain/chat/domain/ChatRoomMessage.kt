package com.chat.proxykotlin.domain.chat.domain

data class ChatRoomMessage (
    val userId:String,
    val content:String,
    val timestamp:Long
)