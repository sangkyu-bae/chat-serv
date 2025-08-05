package com.chat.proxykotlin.config.redis

import io.lettuce.core.ConnectionFuture
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.StringCodec
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig {

    @Value("\${redis.host}")
    lateinit var redisHost: String

    @Value("\${redis.key.room}")
    lateinit var roomKey : String

    @Value("\${redis.key.join-server}")
    lateinit var joinServierKey : String

    @Bean
    fun redisURI(): RedisURI = RedisURI.create(redisHost)

    @Bean
    fun redisClient(redisURI: RedisURI): RedisClient = RedisClient.create(redisURI)

    @Bean
    fun redisConnection(redisClient: RedisClient, redisURI: RedisURI): StatefulRedisConnection<String, String> {
        return redisClient.connect(StringCodec.UTF8, redisURI)
    }

}