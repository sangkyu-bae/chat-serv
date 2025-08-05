package com.chat.proxykotlin.domain.chat.service

import com.chat.proxykotlin.common.converter.ClassConverter.convertTo
import com.chat.proxykotlin.domain.chat.ChatMessage
import com.chat.proxykotlin.domain.chat.dto.ChatUserInfo
import com.chat.proxykotlin.module.kafka.KafkaProducer
import com.chat.proxykotlin.module.redis.RedisKeyManager
import com.chat.proxykotlin.module.redis.RedisRepository
import org.springframework.stereotype.Service
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


@Service
class ChatService (
    private val redisRepository: RedisRepository,
    private val redisKeyManager: RedisKeyManager,
    private val kafkaProducer : KafkaProducer
        ){

    suspend fun test(chatMessage: ChatMessage) = coroutineScope {
        val joinServerKey = redisKeyManager.getJoinServerKey(chatMessage.roomId)

//        val joinUsers = redisRepository.getSetAll(joinServerKey)
        kafkaProducer.send("chat-proxy", chatMessage)
//        joinUsers
//            .map { serverId ->
//                async {
//                    kafkaProducer.send("chat-proxy",serverId, chatMessage)
//                }
//            }
//            .awaitAll()
//            .filterNotNull()
    }
}