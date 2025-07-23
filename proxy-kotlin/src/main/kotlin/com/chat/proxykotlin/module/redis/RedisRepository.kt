package com.chat.proxykotlin.module.redis

import io.lettuce.core.api.StatefulRedisConnection
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Repository

@Repository
class RedisRepository (
    private val connection: StatefulRedisConnection<String, String>
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



}