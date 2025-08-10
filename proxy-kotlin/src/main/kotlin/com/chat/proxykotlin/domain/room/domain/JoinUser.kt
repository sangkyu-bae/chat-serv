package com.chat.proxykotlin.domain.room.domain

import java.time.LocalDateTime

data class JoinUser (
    val id :Long?,
    val room: Room,
    val joinAt:LocalDateTime,
    val userId:String
        )