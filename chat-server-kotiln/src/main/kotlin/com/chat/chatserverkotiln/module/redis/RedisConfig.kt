package com.chat.chatserverkotiln.module.redis

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.data.redis.serializer.RedisSerializationContext

@Configuration
class RedisConfig (
    @Value("\${redis.host}")
    private val redisHost: String,
    ){
    private val log = LoggerFactory.getLogger(RedisConfig::class.java)
    @Bean
    fun reactiveRedisConnectionFactory() : LettuceConnectionFactory{
        log.error("Redis host property resolved: {}", redisHost)
        return LettuceConnectionFactory(redisHost,6379)
    }

    @Bean
    fun reactiveRedisTemplate(factory: LettuceConnectionFactory) : ReactiveRedisTemplate<String,String>{
        val serializationContext = RedisSerializationContext.string()
        return ReactiveRedisTemplate(factory,serializationContext)
    }


    @Bean
    fun reactiveRedisMessageListenerContainer(factory: ReactiveRedisConnectionFactory)
            = ReactiveRedisMessageListenerContainer(factory)
}