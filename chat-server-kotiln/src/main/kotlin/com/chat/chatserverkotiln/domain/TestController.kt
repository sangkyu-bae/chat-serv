package com.chat.chatserverkotiln.domain

import com.chat.chatserverkotiln.module.kafka.KafkaProducer
import com.chat.chatserverkotiln.module.redis.RedisRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class TestController (
    private val redisRepository: RedisRepository,
    private val kafkaProducer: KafkaProducer
        ){

    @GetMapping("/test")
    suspend fun getData():String{
        return "ok";
    }


    @GetMapping("/hello")
    suspend fun hello(): String {
        delay(1.seconds) // ✅ OK
        return "Hello from coroutine!"
    }

    @PostMapping("/set")
    @Operation(summary = "Set 자료구조에 값 추가", description = "지정한 key의 Redis Set에 value를 추가합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "성공적으로 추가됨"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    suspend fun addToSet(
        @Parameter(description = "Set에 저장할 키") @RequestParam key: String,
        @Parameter(description = "Set에 저장할 값") @RequestParam value: String
    ): Long {
        return redisRepository.setTypeSave(key, value)

    }

    @GetMapping("/set")
    @Operation(summary = "Set 자료구조 조회", description = "지정한 key의 Redis Set 값을 모두 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "성공적으로 조회됨"),
            ApiResponse(responseCode = "404", description = "해당 key가 존재하지 않음")
        ]
    )
    suspend fun getSet(
        @Parameter(description = "조회할 Set 키") @RequestParam key: String
    ): List<String> {
        return redisRepository.findWithSetTypeByAll(key)
    }

    @GetMapping("/send/msg")
    @Operation(summary = "kafka 메시지 전송 테스트", description = "kafka 메시지 전송 테스트")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "성공적으로 조회됨"),
            ApiResponse(responseCode = "404", description = "해당 key가 존재하지 않음")
        ]
    )
    suspend fun sendMsg(
        @Parameter(description = "kafka 전송 테스트") @RequestParam key: String
    ): List<String> {
        val result =kafkaProducer.sendMessage("chat","3",key)

        return listOf("메시지 전송 성공", "offset=${result.recordMetadata().offset()}")

    }


}