package com.chat.proxykotlin.module.redis

import com.chat.proxykotlin.config.redis.RedisConfig
import org.springframework.stereotype.Component

@Component
class RedisKeyManager (
    private val redisConfig: RedisConfig
        ){

    fun getChatRoomKey(roomId: String):String{
        return "${redisConfig.roomKey}$roomId"
    }

    fun getJoinServerKey(roomId: String) :String{
        return "${redisConfig.joinServierKey}$roomId"
    }

    fun getChatRoomJoinMemberList(roomId: String):String{
        return "${redisConfig.roomKey}$roomId"
    }
}