package com.chat.proxykotlin.domain.chat.dto

data class ChatUserInfo(
    val userId: String,
    val isConnected : Boolean,
    val connectServer:String,

)