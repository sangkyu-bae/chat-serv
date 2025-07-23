package com.chat.proxykotlin.domain.chat.service

import com.chat.proxykotlin.domain.chat.ChatMessage
import com.chat.proxykotlin.module.redis.RedisKeyManager
import com.chat.proxykotlin.module.redis.RedisRepository
import org.springframework.stereotype.Service

@Service
class ChatService (
    private val redisRepository: RedisRepository,
    private val redisKeyManager: RedisKeyManager
        ){

    suspend fun test(chatMessage: ChatMessage) {
        val roomKey = redisKeyManager.getChatRoomKey(chatMessage.roomId)

        val joinUsers = redisRepository.getSetAll(roomKey)


    }
}