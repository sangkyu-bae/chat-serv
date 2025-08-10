package com.chat.proxykotlin.domain.room.mapper

import com.chat.proxykotlin.domain.room.domain.JoinUser
import com.chat.proxykotlin.domain.room.domain.Room
import com.chat.proxykotlin.domain.room.entity.JoinUserEntity
import com.chat.proxykotlin.domain.room.entity.RoomEntity

fun RoomEntity.toDomain(): Room =
    Room(
        id = this.id ?: 0,
        name = this.name,
        createAt = this.createAt
    )

fun Room.toEntity(): RoomEntity =
    RoomEntity(
        id = if (this.id == 0L) null else this.id,
        name = this.name,
        createAt = this.createAt
    )

fun JoinUser.toEntity(): JoinUserEntity =
    JoinUserEntity(
        id = if (this.id == 0L) null else this.id,
        roomEntity = this.room.toEntity(),
        joinAt = this.joinAt,
        userId = this.userId
    )

fun JoinUserEntity.toDomain(): JoinUser =
    JoinUser(
        id = if (this.id == 0L) null else this.id,
        room = this.roomEntity.toDomain(),
        joinAt = this.joinAt,
        userId = this.userId
    )



