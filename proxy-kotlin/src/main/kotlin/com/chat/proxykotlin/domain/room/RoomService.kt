package com.chat.proxykotlin.domain.room

import com.chat.proxykotlin.domain.room.domain.Room
import com.chat.proxykotlin.domain.room.entity.RoomEntity
import com.chat.proxykotlin.domain.room.mapper.toDomain
import com.chat.proxykotlin.domain.room.mapper.toEntity
import com.chat.proxykotlin.domain.room.repository.RoomRepository
import org.springframework.stereotype.Service

@Service
class RoomService (
    private val roomRepository: RoomRepository
        ){
    fun insert(room: Room): Room {
        val roomEntity :RoomEntity = room.toEntity()
        return roomRepository.save(roomEntity).toDomain()
    }
}