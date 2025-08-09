package com.chat.proxykotlin.domain.room.repository

import com.chat.proxykotlin.domain.room.entity.RoomEntity
import org.springframework.data.jpa.repository.JpaRepository


interface RoomRepository : JpaRepository<RoomEntity,Long> {
}