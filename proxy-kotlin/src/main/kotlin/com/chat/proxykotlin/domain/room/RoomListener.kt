package com.chat.proxykotlin.domain.room

import com.chat.proxykotlin.domain.room.dto.JoinServerUser
import com.chat.proxykotlin.domain.room.service.RoomService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class RoomListener(
    private val objectMapper: ObjectMapper,
    private val roomService: RoomService
) {

    private val log =LoggerFactory.getLogger(RoomListener::class.java)

    @KafkaListener(topics = ["\${spring.kafka.topic.room.joinserver}"])
    suspend fun joinServerRoomListener(message : String){
        log.info(message)
        val joinServerUser = objectMapper.readValue(message,JoinServerUser::class.java)
        roomService.insertJoinUserServer(joinServerUser)
    }
}