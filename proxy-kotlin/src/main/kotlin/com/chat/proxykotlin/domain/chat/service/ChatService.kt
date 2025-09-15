package com.chat.proxykotlin.domain.chat.service

import com.chat.proxykotlin.domain.chat.ChatListener
import com.chat.proxykotlin.domain.chat.ChatMessage
import com.chat.proxykotlin.module.kafka.KafkaProducer
import com.chat.proxykotlin.module.redis.RedisKeyManager
import com.chat.proxykotlin.module.redis.RedisRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service




@Service
class ChatService (
    private val redisRepository: RedisRepository,
    private val redisKeyManager: RedisKeyManager,
    private val kafkaProducer : KafkaProducer
        ){
    val mapper = jacksonObjectMapper()

    private val log = LoggerFactory.getLogger(ChatService::class.java)
    suspend fun sendMsg(chatMessage: ChatMessage)  {
        val joinServerKey = redisKeyManager.getJoinServerKey(chatMessage.roomId)
        val serverList = redisRepository.getSetAll(joinServerKey)

        log.info("serverList : {}",serverList)
        log.info("joinServerKet : {}",joinServerKey)
        val sendMsg = mapper.writeValueAsString(chatMessage)
        for (server in serverList) {
            redisRepository.publishSuspend(server, sendMsg)
        }
    }
}