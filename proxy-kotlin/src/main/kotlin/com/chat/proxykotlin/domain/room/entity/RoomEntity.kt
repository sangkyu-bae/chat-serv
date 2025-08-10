package com.chat.proxykotlin.domain.room.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "room")
class RoomEntity(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "room_name")
    val name: String,

    @Column(name = "create_at")
    val createAt: LocalDateTime,

    @OneToMany(mappedBy = "roomEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var joinUserEntities: MutableList<JoinUserEntity> = mutableListOf()

){
    fun addJoinUser(joinUserEntity: JoinUserEntity) {
        joinUserEntities.add(joinUserEntity)
        joinUserEntity.roomEntity = this
    }
}

