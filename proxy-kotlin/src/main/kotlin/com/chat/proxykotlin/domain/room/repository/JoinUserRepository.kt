package com.chat.proxykotlin.domain.room.repository

import com.chat.proxykotlin.domain.room.entity.JoinUserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JoinUserRepository : JpaRepository<JoinUserEntity,Long> {
}