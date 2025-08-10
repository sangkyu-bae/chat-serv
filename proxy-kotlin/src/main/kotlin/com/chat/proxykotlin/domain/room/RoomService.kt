package com.chat.proxykotlin.domain.room

import com.chat.proxykotlin.domain.room.domain.JoinUser
import com.chat.proxykotlin.domain.room.domain.Room
import com.chat.proxykotlin.domain.room.entity.JoinUserEntity
import com.chat.proxykotlin.domain.room.entity.RoomEntity
import com.chat.proxykotlin.domain.room.mapper.toDomain
import com.chat.proxykotlin.domain.room.mapper.toEntity
import com.chat.proxykotlin.domain.room.repository.JoinUserRepository
import com.chat.proxykotlin.domain.room.repository.RoomRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class RoomService (
    private val roomRepository: RoomRepository,
    private val joinUserRepository: JoinUserRepository
        ){
    fun insert(room: Room, joinUsers: List<JoinUser>): Room {

        val roomEntity = room.toEntity()


        val joinUserEntities = joinUsers.map { joinUser ->
            val entity = joinUser.toEntity()
            entity.roomEntity = roomEntity
            entity
        }.toMutableList()

        roomEntity.joinUserEntities = joinUserEntities

        val savedRoomEntity = roomRepository.save(roomEntity)
        return savedRoomEntity.toDomain()

    }
}