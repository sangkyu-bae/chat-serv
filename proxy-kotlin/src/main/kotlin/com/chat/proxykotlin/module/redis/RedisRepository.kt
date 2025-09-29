package com.chat.proxykotlin.module.redis

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.api.StatefulRedisConnection
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Repository

@Repository
class RedisRepository (
    private val connection: StatefulRedisConnection<String, String>,
    private val objectMapper: ObjectMapper
        ){

    private val asyncCommands = connection.async()

    suspend fun hget(key:String):String?{
        return asyncCommands.get(key).await()
    }
    //hash
    suspend fun getHashByFiled(key: String, field: String): String? {
        return asyncCommands.hget(key, field).await()
    }

    suspend fun getHashAllFields(key: String): Map<String, String> {
        return asyncCommands.hgetall(key).await()
    }

    suspend fun isHashFieldExists(key: String, field: String): Boolean {
        return asyncCommands.hexists(key, field).await()
    }
    //set
    suspend fun getSetAll(key: String): Set<String> {
        return asyncCommands.smembers(key).await()
    }

    suspend fun addSet(key: String, value: String): Long {
        return asyncCommands.sadd(key, value).await()
    }
    suspend fun isSetExist(key: String, value: String): Boolean {
        return asyncCommands.sismember(key, value).await()
    }

    suspend fun removeSet(key: String, value: String): Long {
        return asyncCommands.srem(key, value).await()
    }
    //list
    suspend fun getListAll(key: String): List<String> {
        return asyncCommands.lrange(key, 0, -1).await()
    }

    suspend fun getListLast(key: String): String? {
        return asyncCommands.rpop(key).await()
    }

    suspend fun insertList(key: String, value: String): Long {
        return asyncCommands.rpush(key, value).await()
    }

    suspend fun publishSuspend(channel: String, payload: String): Long {
        return asyncCommands.publish(channel, payload).await()
    }
    suspend fun getRecentValues(key: String, limit: Long = 50): List<String> {
        return asyncCommands.zrevrange(key, 0, limit - 1).await() ?: emptyList()
    }

    suspend fun addValueWithTrim(key: String, value: String, maxSize: Long = 50) {
        val score = System.currentTimeMillis().toDouble()
        asyncCommands.zadd(key, score, value).await()
        // 오래된 것 정리 (뒤에서부터 maxSize 이후는 삭제)
        asyncCommands.zremrangebyrank(key, 0, -maxSize-1).await()
    }

}