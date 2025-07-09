//package com.chat.chatserverkotiln.domain.chat.controller
//
//import com.chat.chatserverkotiln.module.websocket.MessageBroadcastService
//import com.chat.chatserverkotiln.module.websocket.ChatMessage
//import org.springframework.web.bind.annotation.*
//import reactor.core.publisher.Mono
//
//@RestController
//@RequestMapping("/api/chat")
//class ChatApiController(
//    private val messageBroadcastService: MessageBroadcastService
//) {
//
//    /**
//     * 특정 방에 메시지 브로드캐스팅 (API 서버에서 호출)
//     */
//    @PostMapping("/broadcast/{roomId}")
//    fun broadcastToRoom(
//        @PathVariable roomId: String,
//        @RequestBody message: ChatMessage
//    ): Mono<Void> {
//        return messageBroadcastService.broadcastToRoom(roomId, message)
//    }
//
//    /**
//     * 특정 사용자에게 메시지 전송 (API 서버에서 호출)
//     */
//    @PostMapping("/send/{userId}")
//    fun sendToUser(
//        @PathVariable userId: String,
//        @RequestBody message: ChatMessage
//    ): Mono<Void> {
//        return messageBroadcastService.sendToUser(userId, message)
//    }
//
//    /**
//     * 방의 사용자 목록 조회
//     */
//    @GetMapping("/room/{roomId}/users")
//    fun getRoomUsers(@PathVariable roomId: String): Mono<Set<String>> {
//        return messageBroadcastService.getRoomUsers(roomId)
//    }
//
//    /**
//     * 사용자를 방에 추가
//     */
//    @PostMapping("/room/{roomId}/users/{userId}")
//    fun addUserToRoom(
//        @PathVariable roomId: String,
//        @PathVariable userId: String
//    ): Mono<Void> {
//        return messageBroadcastService.addUserToRoom(roomId, userId)
//    }
//
//    /**
//     * 사용자를 방에서 제거
//     */
//    @DeleteMapping("/room/{roomId}/users/{userId}")
//    fun removeUserFromRoom(
//        @PathVariable roomId: String,
//        @PathVariable userId: String
//    ): Mono<Void> {
//        return messageBroadcastService.removeUserFromRoom(roomId, userId)
//    }
//}