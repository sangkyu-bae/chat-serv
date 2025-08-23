package com.chat.proxykotlin.domain.room.controller
import com.chat.proxykotlin.domain.room.RoomService
import com.chat.proxykotlin.domain.room.domain.JoinUser
import com.chat.proxykotlin.domain.room.domain.Room
import com.chat.proxykotlin.domain.room.dto.JoinRoom
import com.chat.proxykotlin.domain.room.dto.RegisterRoom
import lombok.extern.slf4j.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@RestController("/api/chat-proxy/room")
@Slf4j
class RoomController (
    private val roomService: RoomService
        ){

    @PostMapping("/")
    @Operation(summary = "채팅방 생성", description = "채팅방 생성 합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "성공적으로 추가됨"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    fun registerRoom(@RequestBody registerRoom: RegisterRoom,
                     @RequestHeader("userId") userId: String ) : ResponseEntity<Room> {

        val roomKey :String = UUID.randomUUID().toString() + ":" + userId
        val room : Room = Room(null,roomKey, LocalDateTime.now())
        val joinUserList : List<JoinUser> = registerRoom.joinList.map{ joinUser ->
            JoinUser(null,room, LocalDateTime.now(),joinUser)
        }

        return ResponseEntity.ok(roomService.insert(room,joinUserList))
    }

    @PostMapping("/join")
    @Operation(summary = "채팅방 입장", description = "채팅방에 입장 합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "성공적으로 추가됨"),
            ApiResponse(responseCode = "500", description = "서버 오류")
        ]
    )
    fun joinRooom(@RequestBody joinRoom: JoinRoom,
                  @RequestHeader("userId") userId: String){

    }
}