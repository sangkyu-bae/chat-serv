package com.chat.proxykotlin.domain.room.domain

import java.time.LocalDateTime

data class Room (
    val id :Long ?,
    val name : String,
    val createAt : LocalDateTime
        )