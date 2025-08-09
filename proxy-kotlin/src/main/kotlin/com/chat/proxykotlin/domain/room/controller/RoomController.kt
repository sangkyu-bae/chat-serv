package com.chat.proxykotlin.domain.room.controller
import com.chat.proxykotlin.domain.room.RoomService
import com.chat.proxykotlin.domain.room.domain.Room
import com.chat.proxykotlin.domain.room.dto.RegisterRoom
import lombok.extern.slf4j.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController("/api/chat-proxy/room")
@Slf4j
class RoomController (
    private val roomService: RoomService
        ){

    @PostMapping("/")
    fun registerRoom(@RequestBody registerRoom: RegisterRoom,
                     @RequestHeader("userId") userId: String ) : ResponseEntity<Room> {

        val roomKey :String = UUID.randomUUID().toString() + ":" + userId
        val room : Room = Room(null,roomKey, LocalDateTime.now())

        return ResponseEntity.ok(roomService.insert(room))
    }
}