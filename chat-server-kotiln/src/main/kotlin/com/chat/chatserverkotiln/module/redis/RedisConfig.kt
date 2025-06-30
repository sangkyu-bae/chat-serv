package com.chat.chatserverkotiln.module.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext

@Configuration
class RedisConfig {
    @Bean
    fun reactiveRedisConnectionFactory() : LettuceConnectionFactory{
        return LettuceConnectionFactory("localhost",6379)
    }

    @Bean
    fun reactiveRedisTemplate(factory: LettuceConnectionFactory) : ReactiveRedisTemplate<String,String>{
        val serializationContext = RedisSerializationContext.string()
        return ReactiveRedisTemplate(factory,serializationContext)
    }
}