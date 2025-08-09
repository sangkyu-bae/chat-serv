package com.chat.proxykotlin.domain.room.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "join_user")
class JoinUserEntity(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "room_id")
    val roomEntity: RoomEntity,

    @Column(name = "join_at")
    val joinAt: LocalDateTime
)