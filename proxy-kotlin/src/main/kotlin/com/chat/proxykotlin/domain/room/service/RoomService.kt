package com.chat.proxykotlin.domain.room.service

import aj.org.objectweb.asm.TypeReference
import com.chat.proxykotlin.common.converter.ClassConverter
import com.chat.proxykotlin.common.converter.ClassConverter.readList
import com.chat.proxykotlin.domain.chat.domain.ChatRoomMessage
import com.chat.proxykotlin.domain.room.domain.JoinUser
import com.chat.proxykotlin.domain.room.domain.Room
import com.chat.proxykotlin.domain.room.domain.RoomChat
import com.chat.proxykotlin.domain.room.dto.JoinServerUser
import com.chat.proxykotlin.domain.room.entity.JoinUserEntity
import com.chat.proxykotlin.domain.room.entity.RoomEntity
import com.chat.proxykotlin.domain.room.mapper.toDomain
import com.chat.proxykotlin.domain.room.mapper.toEntity
import com.chat.proxykotlin.domain.room.repository.JoinUserRepository
import com.chat.proxykotlin.domain.room.repository.RoomRepository
import com.chat.proxykotlin.module.redis.RedisKeyManager
import com.chat.proxykotlin.module.redis.RedisRepository
import com.fasterxml.jackson.databind.ObjectMapper
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Slf4j
@Transactional
class RoomService (
    private val roomRepository: RoomRepository,
    private val redisRepository: RedisRepository,
    private val redisKeyManager: RedisKeyManager,
    private val joinUserRepository: JoinUserRepository,
    private val objectMapper: ObjectMapper
        ){

    suspend fun joinUser(room:Room, joinUser: JoinUser) : JoinUser{
        val roomEntity : RoomEntity= room.toEntity()
        val joinUserEntity: JoinUserEntity = joinUser.toEntity()

        roomEntity.addJoinUser(joinUserEntity)
        val saveRoomEntity = roomRepository.save(roomEntity)

        val userKey = redisKeyManager.getUserInfoKey(joinUser.userId)
        val joinServer = requireNotNull(
            redisRepository.getHashByFiled(userKey, "server")
        ) { "userId=${joinUser.userId} 의 server 해시 필드가 없음" }


        val joinServerKey = redisKeyManager.getJoinServerKey(room.name)
        redisRepository.addSet(joinServerKey,joinServer)


        return saveRoomEntity.joinUserEntities.last().toDomain()
    }


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

    suspend fun insertJoinUserServer(joinServerUser: JoinServerUser){
        val joinUser :List<JoinUserEntity> = joinUserRepository.findByUserId(joinServerUser.userId)


        for(user in joinUser){
            val roomId = user.roomEntity.name
            val roomKey = redisKeyManager.getJoinServerKey(roomId)
            redisRepository.addSet(roomKey,joinServerUser.serverId)
        }
    }

    suspend fun readRoomByUser(userId: String) : List<RoomChat>{
        val joinUser : List<JoinUserEntity> = joinUserRepository.findByUserId(userId)
        val roomChatList: MutableList<RoomChat> = mutableListOf()
        for(user in joinUser){
            val roomId = user.roomEntity.name
            val charRoomKey = redisKeyManager.getChatRoomKey(roomId)

            val chat : List<String> = redisRepository.getRecentValues(charRoomKey)
            val chats: List<ChatRoomMessage> = objectMapper.readList(chat)

            roomChatList.add(RoomChat(roomId,chats))
        }

        return roomChatList
    }
}