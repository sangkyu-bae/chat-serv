package com.chat.proxykotlin.domain.room.domain

import com.chat.proxykotlin.domain.chat.domain.ChatRoomMessage

data class RoomChat (
    val roomId : String,
    val chatList : List<ChatRoomMessage>
)