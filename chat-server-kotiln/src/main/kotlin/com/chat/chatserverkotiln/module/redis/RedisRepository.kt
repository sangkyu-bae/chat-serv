package com.chat.chatserverkotiln.module.redis

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveSetOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class RedisRepository(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val setOps: ReactiveSetOperations<String, String>
        get() = reactiveRedisTemplate.opsForSet()

    suspend fun setTypeSave(key: String, value: String): Long {
//        return setOps.add(key, value)
        return setOps.add(key, value).awaitSingle()
    }
    fun setTypeSaveByWebFlux(key: String, value: String): Mono<Long> {
        return reactiveRedisTemplate.opsForSet().add(key, value)
    }
    suspend fun findWithSetTypeByAll(key: String): List<String> {
        return setOps.members(key)
            .collectList()
            .awaitSingle()
    }

    suspend fun saveWithHash(key: String, info: Map<String, String>): Boolean {
        return reactiveRedisTemplate.opsForHash<String, String>()
            .putAll(key, info)
            .awaitSingle()
    }

    fun saveWithHashByWebFlux(key: String, info: Map<String, String>): Mono<Boolean> {
        return reactiveRedisTemplate.opsForHash<String, String>()
            .putAll(key, info)
            .then(Mono.just(true))
    }
    suspend fun deleteHash(key: String): Long {
        return reactiveRedisTemplate.delete(key).awaitSingle()
    }
    
    suspend fun removeFromSet(key: String, value: String): Long {
        return setOps.remove(key, value).awaitSingle()
    }
    
    suspend fun countKeys(pattern: String): Long {
        return reactiveRedisTemplate.keys(pattern)
            .count()
            .awaitSingle()
    }
    
    fun listTypeSave(key: String, value: String): Mono<Long> {
        return reactiveRedisTemplate.opsForList().rightPush(key, value)
    }

    fun findWithListType(key: String, start: Long, end: Long): Flux<String> {
        return reactiveRedisTemplate.opsForList().range(key, start, end)
    }

    fun findWithListTypeByAll(key: String): Flux<String> {
        return findWithListType(key, 0, -1)
    }

    fun findByHash(key: String, subKey: String): Mono<String> {
        return reactiveRedisTemplate.opsForHash<String, String>().get(key, subKey)
    }

    fun existsInSet(key: String, value: String): Mono<Boolean> {
        return setOps.isMember(key, value)
            .map { it == true } // null 안전 처리
    }
}